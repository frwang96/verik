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

import io.verik.core.main.LineException
import io.verik.core.kt.*
import io.verik.core.symbol.Symbol

enum class VkxActionBlockType {
    PUT,
    REG,
    INITIAL;

    companion object {

        operator fun invoke(annotations: List<KtAnnotationFunction>, line: Int): VkxActionBlockType {
            if (annotations.isEmpty()) {
                throw LineException("action block annotations expected", line)
            }
            if (annotations.size > 1) {
                throw LineException("illegal action block type", line)
            }
            return when (annotations[0]) {
                KtAnnotationFunction.ABSTRACT -> throw LineException("action blocks cannot be abstract", line)
                KtAnnotationFunction.PUT -> PUT
                KtAnnotationFunction.REG -> REG
                KtAnnotationFunction.INITIAL -> INITIAL
                KtAnnotationFunction.TASK -> throw LineException("illegal action block type", line)
            }
        }
    }
}

data class VkxActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: VkxActionBlockType,
        val edges: List<VkxEdge>?,
        val block: VkxBlock
): VkxDeclaration {

    companion object {

        fun isActionBlock(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationFunction && declaration.annotations.any {
                it in listOf(
                        KtAnnotationFunction.PUT,
                        KtAnnotationFunction.REG,
                        KtAnnotationFunction.INITIAL
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkxActionBlock {
            val declarationFunction = declaration.let {
                if (it is KtDeclarationFunction) it
                else throw LineException("function declaration expected", it)
            }
            val actionBlockType = VkxActionBlockType(declarationFunction.annotations, declarationFunction.line)
            val (block, sensitivityEdges) = getBlockAndEdges(declarationFunction)

            if (actionBlockType == VkxActionBlockType.REG) {
                if (sensitivityEdges == null) {
                    throw LineException("edges expected for reg block", declarationFunction)
                }
            } else {
                if (sensitivityEdges != null) {
                    throw LineException("edges not permitted here", declarationFunction)
                }
            }

            return VkxActionBlock(
                    declarationFunction.line,
                    declarationFunction.identifier,
                    declarationFunction.symbol,
                    actionBlockType,
                    sensitivityEdges,
                    block
            )
        }

        private fun getBlockAndEdges(declarationFunction: KtDeclarationFunction): Pair<VkxBlock, List<VkxEdge>?> {
            val isOnExpression = { it: KtStatement ->
                it.expression is KtExpressionOperator && it.expression.identifier == KtOperatorIdentifier.LAMBDA_ON
            }
            return if (declarationFunction.block.statements.any { isOnExpression(it) }) {
                if (declarationFunction.block.statements.size != 1) {
                    throw LineException("illegal use of on expression", declarationFunction)
                }
                val onExpression = declarationFunction.block.statements[0].expression as KtExpressionOperator
                val edges = onExpression.args.map { VkxEdge(it) }
                val block = VkxBlock(onExpression.blocks[0])
                Pair(block, edges)
            } else Pair(VkxBlock(declarationFunction.block), null)
        }
    }
}
