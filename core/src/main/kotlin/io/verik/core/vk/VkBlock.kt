/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.core.vk

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.kt.KtRuleType
import io.verik.core.sv.SvBlock
import io.verik.core.sv.SvBlockType
import io.verik.core.sv.SvContinuousAssignment

enum class VkBlockType {
    PUT,
    REG,
    DRIVE,
    INITIAL;

    fun extract(fileLine: FileLine): SvBlockType {
        return when (this) {
            PUT -> SvBlockType.ALWAYS_COMB
            REG -> SvBlockType.ALWAYS_FF
            DRIVE -> throw FileLineException("drive block not supported", fileLine)
            INITIAL -> SvBlockType.INITIAL
        }
    }

    companion object {

        operator fun invoke(annotations: List<VkFunctionAnnotation>, fileLine: FileLine): VkBlockType {
            return if (annotations.size == 1) {
                when (annotations[0]) {
                    VkFunctionAnnotation.PUT -> PUT
                    VkFunctionAnnotation.REG -> REG
                    VkFunctionAnnotation.DRIVE -> DRIVE
                    VkFunctionAnnotation.INITIAL -> INITIAL
                    else -> throw FileLineException("illegal block type", fileLine)
                }
            } else throw FileLineException("illegal block type", fileLine)
        }
    }
}

data class VkBlock(
        val type: VkBlockType,
        val sensitivityEntries: List<VkSensitivityEntry>,
        val statements: List<VkStatement>,
        val fileLine: FileLine
) {

    fun extractContinuousAssignment(): SvContinuousAssignment? {
        return if (type == VkBlockType.PUT && statements.size == 1) {
            val statement = statements[0]
            if (statement.expression is VkOperatorExpression && statement.expression.type == VkOperatorType.PUT) {
                SvContinuousAssignment(statement.expression.extractExpression(), fileLine)
            } else null
        } else null
    }

    fun extractBlock(): SvBlock {
        val svType = type.extract(fileLine)
        val svSensitivityEntries = sensitivityEntries.map { it.extract() }
        val svStatements = statements.map { it.extract() }
        return SvBlock(svType, svSensitivityEntries, svStatements, fileLine)
    }

    companion object {

        fun isBlock(functionDeclaration: VkFunctionDeclaration) = functionDeclaration.annotations.any {
            it in listOf(
                    VkFunctionAnnotation.PUT,
                    VkFunctionAnnotation.REG,
                    VkFunctionAnnotation.DRIVE,
                    VkFunctionAnnotation.INITIAL
            )
        }

        operator fun invoke(functionDeclaration: VkFunctionDeclaration): VkBlock {
            val blockType = VkBlockType(functionDeclaration.annotations, functionDeclaration.fileLine)
            if (functionDeclaration.modifiers.isNotEmpty()) {
                throw FileLineException("function modifiers are not permitted here", functionDeclaration.fileLine)
            }

            val statements = if (functionDeclaration.body != null) {
                val blockOrExpression = functionDeclaration.body.firstAsRule()
                when (blockOrExpression.type) {
                    KtRuleType.BLOCK -> {
                        blockOrExpression.firstAsRule().childrenAs(KtRuleType.STATEMENT).map { VkStatement(it) }
                    }
                    else -> throw FileLineException("block expected", blockOrExpression.fileLine)
                }
            } else listOf()

            return if (blockType == VkBlockType.REG) {
                parseRegBlock(statements, functionDeclaration.fileLine)
            } else {
                VkBlock(blockType, listOf(), statements, functionDeclaration.fileLine)
            }
        }

        private fun parseRegBlock(statements: List<VkStatement>, fileLine: FileLine): VkBlock {
            if (statements.size != 1) {
                throw FileLineException("on expression expected", fileLine)
            }
            val expression = statements[0].expression
            return if (expression is VkCallableExpression
                    && expression.target is VkIdentifierExpression
                    && expression.target.identifier == "on") {
                if (expression.args.size < 2) {
                    throw FileLineException("sensitivity entries expected", fileLine)
                }
                val sensitivityEntries = expression.args.dropLast(1).map { VkSensitivityEntry(it) }
                val lambdaStatements = expression.args.last().let {
                    if (it is VkLambdaExpression) {
                        it.statements
                    } else throw FileLineException("lambda expression expected", fileLine)
                }
                VkBlock(VkBlockType.REG, sensitivityEntries, lambdaStatements, fileLine)
            } else throw FileLineException("on expression expected", fileLine)
        }
    }
}