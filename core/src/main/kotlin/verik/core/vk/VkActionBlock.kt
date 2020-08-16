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

package verik.core.vk

import verik.core.kt.KtAnnotationFunction
import verik.core.kt.KtOperatorIdentifier
import verik.core.ktx.KtxDeclaration
import verik.core.ktx.KtxDeclarationFunction
import verik.core.ktx.KtxExpressionOperator
import verik.core.ktx.KtxStatement
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

enum class VkActionBlockType {
    PUT,
    REG,
    INITIAL;

    companion object {

        operator fun invoke(annotations: List<KtAnnotationFunction>, line: Int): VkActionBlockType {
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

data class VkActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: VkActionBlockType,
        val edges: List<VkEdge>?,
        val block: VkBlock
): VkDeclaration {

    companion object {

        fun isActionBlock(declaration: KtxDeclaration): Boolean {
            return declaration is KtxDeclarationFunction && declaration.annotations.any {
                it in listOf(
                        KtAnnotationFunction.PUT,
                        KtAnnotationFunction.REG,
                        KtAnnotationFunction.INITIAL
                )
            }
        }

        operator fun invoke(declaration: KtxDeclaration): VkActionBlock {
            val declarationFunction = declaration.let {
                if (it is KtxDeclarationFunction) it
                else throw LineException("function declaration expected", it)
            }

            val actionBlockType = VkActionBlockType(declarationFunction.annotations, declarationFunction.line)
            val (block, sensitivityEdges) = getBlockAndEdges(declarationFunction)

            if (actionBlockType == VkActionBlockType.REG) {
                if (sensitivityEdges == null) {
                    throw LineException("edges expected for reg block", declarationFunction)
                }
            } else {
                if (sensitivityEdges != null) {
                    throw LineException("edges not permitted here", declarationFunction)
                }
            }

            return VkActionBlock(
                    declarationFunction.line,
                    declarationFunction.identifier,
                    declarationFunction.symbol,
                    actionBlockType,
                    sensitivityEdges,
                    block
            )
        }

        private fun getBlockAndEdges(declarationFunction: KtxDeclarationFunction): Pair<VkBlock, List<VkEdge>?> {
            val isOnExpression = { it: KtxStatement ->
                it.expression is KtxExpressionOperator && it.expression.identifier == KtOperatorIdentifier.LAMBDA_ON
            }
            return if (declarationFunction.block.statements.any { isOnExpression(it) }) {
                if (declarationFunction.block.statements.size != 1) {
                    throw LineException("illegal use of on expression", declarationFunction)
                }
                val onExpression = declarationFunction.block.statements[0].expression as KtxExpressionOperator
                val edges = onExpression.args.map { VkEdge(it) }
                val block = VkBlock(onExpression.blocks[0])
                Pair(block, edges)
            } else Pair(VkBlock(declarationFunction.block), null)
        }
    }
}
