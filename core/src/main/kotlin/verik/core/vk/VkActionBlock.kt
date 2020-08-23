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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_ON

enum class VkActionBlockType {
    PUT,
    REG,
    RUN;

    companion object {

        operator fun invoke(annotations: List<KtAnnotationFunction>, line: Int): VkActionBlockType {
            if (annotations.isEmpty()) {
                throw LineException("action block annotations expected", line)
            }
            if (annotations.size > 1) {
                throw LineException("illegal action block type", line)
            }
            return when (annotations[0]) {
                KtAnnotationFunction.PUT -> PUT
                KtAnnotationFunction.REG -> REG
                KtAnnotationFunction.RUN -> RUN
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
        val eventExpressions: List<VkExpression>,
        val block: VkBlock
): VkDeclaration {

    companion object {

        fun isActionBlock(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationFunction && declaration.annotations.any {
                it in listOf(
                        KtAnnotationFunction.PUT,
                        KtAnnotationFunction.REG,
                        KtAnnotationFunction.RUN
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkActionBlock {
            val declarationFunction = declaration.let {
                if (it is KtDeclarationFunction) it
                else throw LineException("function declaration expected", it)
            }

            val actionBlockType = VkActionBlockType(declarationFunction.annotations, declarationFunction.line)
            val (block, eventExpressions) = getBlockAndEventExpressions(declarationFunction)

            if (actionBlockType == VkActionBlockType.REG) {
                if (eventExpressions.isEmpty()) {
                    throw LineException("on expression expected for reg block", declarationFunction)
                }
            } else {
                if (eventExpressions.isNotEmpty()) {
                    throw LineException("on expression not permitted here", declarationFunction)
                }
            }

            return VkActionBlock(
                    declarationFunction.line,
                    declarationFunction.identifier,
                    declarationFunction.symbol,
                    actionBlockType,
                    eventExpressions,
                    block
            )
        }

        private fun getBlockAndEventExpressions(declarationFunction: KtDeclarationFunction): Pair<VkBlock, List<VkExpression>> {
            val isOnExpression = { it: KtStatement ->
                it is KtStatementExpression
                        && it.expression is KtExpressionOperator
                        && it.expression.operator == OPERATOR_ON
            }
            return if (declarationFunction.block.statements.any { isOnExpression(it) }) {
                if (declarationFunction.block.statements.size != 1) {
                    throw LineException("illegal use of on expression", declarationFunction)
                }
                val statementExpression = declarationFunction.block.statements[0] as KtStatementExpression
                val onExpression = statementExpression.expression as KtExpressionOperator
                val block = VkBlock(onExpression.blocks[0])
                val eventExpressions = onExpression.args.map { VkExpression(it) }
                Pair(block, eventExpressions)
            } else Pair(VkBlock(declarationFunction.block), listOf())
        }
    }
}
