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

import io.verik.core.Line
import io.verik.core.LineException
import io.verik.core.al.AlRuleType
import io.verik.core.sv.SvBlock
import io.verik.core.sv.SvBlockType
import io.verik.core.sv.SvContinuousAssignment

enum class VkBlockType {
    PUT,
    REG,
    DRIVE,
    INITIAL;

    fun extract(line: Int): SvBlockType {
        return when (this) {
            PUT -> SvBlockType.ALWAYS_COMB
            REG -> SvBlockType.ALWAYS_FF
            DRIVE -> throw LineException("drive block not supported", line)
            INITIAL -> SvBlockType.INITIAL
        }
    }

    companion object {

        operator fun invoke(annotations: List<VkFunctionAnnotation>, line: Int): VkBlockType {
            if (VkFunctionAnnotation.ABSTRACT in annotations) {
                throw LineException("blocks cannot be abstract", line)
            }
            return if (annotations.size == 1) {
                when (annotations[0]) {
                    VkFunctionAnnotation.PUT -> PUT
                    VkFunctionAnnotation.REG -> REG
                    VkFunctionAnnotation.DRIVE -> DRIVE
                    VkFunctionAnnotation.INITIAL -> INITIAL
                    else -> throw LineException("illegal block type", line)
                }
            } else throw LineException("illegal block type", line)
        }
    }
}

data class VkBlock(
        override val line: Int,
        val type: VkBlockType,
        val sensitivityEntries: List<VkSensitivityEntry>,
        val statements: List<VkStatement>
): Line {

    fun extractContinuousAssignment(): SvContinuousAssignment? {
        return if (type == VkBlockType.PUT && statements.size == 1) {
            val statement = statements[0]
            if (statement.expression is VkExpressionOperator && statement.expression.type == VkOperatorType.PUT) {
                SvContinuousAssignment(line, statement.expression.extractExpression())
            } else null
        } else null
    }

    fun extractBlock(): SvBlock {
        val svType = type.extract(line)
        val svSensitivityEntries = sensitivityEntries.map { it.extract() }
        val svStatements = statements.map { it.extract() }
        return SvBlock(line, svType, svSensitivityEntries, svStatements)
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
            val blockType = VkBlockType(functionDeclaration.annotations, functionDeclaration.line)
            if (functionDeclaration.modifiers.isNotEmpty()) {
                throw LineException("function modifiers are not permitted here", functionDeclaration)
            }

            val statements = if (functionDeclaration.body != null) {
                val blockOrExpression = functionDeclaration.body.firstAsRule()
                when (blockOrExpression.type) {
                    AlRuleType.BLOCK -> {
                        blockOrExpression.firstAsRule().childrenAs(AlRuleType.STATEMENT).map { VkStatement(it) }
                    }
                    else -> throw LineException("block expected", blockOrExpression)
                }
            } else listOf()

            return if (blockType == VkBlockType.REG) {
                parseRegBlock(statements, functionDeclaration.line)
            } else {
                VkBlock(functionDeclaration.line, blockType, listOf(), statements)
            }
        }

        private fun parseRegBlock(statements: List<VkStatement>, line: Int): VkBlock {
            if (statements.size != 1) {
                throw LineException("on expression expected", line)
            }
            val expression = statements[0].expression
            return if (expression is VkExpressionCallable
                    && expression.target is VkExpressionIdentifier
                    && expression.target.identifier == "on") {
                if (expression.args.size < 2) {
                    throw LineException("sensitivity entries expected", line)
                }
                val sensitivityEntries = expression.args.dropLast(1).map { VkSensitivityEntry(it) }
                val lambdaStatements = expression.args.last().let {
                    if (it is VkExpressionLambda) {
                        it.statements
                    } else throw LineException("lambda expression expected", line)
                }
                VkBlock(line, VkBlockType.REG, sensitivityEntries, lambdaStatements)
            } else throw LineException("on expression expected", line)
        }
    }
}