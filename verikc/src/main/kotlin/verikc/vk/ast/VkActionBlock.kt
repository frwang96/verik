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

package verikc.vk.ast

import verikc.base.ast.ActionBlockType
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_ON

data class VkActionBlock(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val actionBlockType: ActionBlockType,
    val eventExpressions: List<VkExpression>,
    val block: VkBlock
): VkDeclaration {

    companion object {

        fun isActionBlock(declaration: KtDeclaration): Boolean {
            return declaration is KtFunction && declaration.annotations.any {
                it in listOf(
                    KtAnnotationFunction.COM,
                    KtAnnotationFunction.SEQ,
                    KtAnnotationFunction.RUN
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkActionBlock {
            val function = declaration.let {
                if (it is KtFunction) it
                else throw LineException("function declaration expected", it.line)
            }

            val actionBlockType = getActionBlockType(function.annotations, function.line)
            val (mainBlock, eventExpressions) = getMainBlockAndEventExpressions(function.block, function.line)

            if (actionBlockType == ActionBlockType.SEQ) {
                if (eventExpressions.isEmpty()) {
                    throw LineException("on expression expected for seq block", function.line)
                }
            } else {
                if (eventExpressions.isNotEmpty()) {
                    throw LineException("on expression not permitted here", function.line)
                }
            }

            return VkActionBlock(
                function.line,
                function.identifier,
                function.symbol,
                actionBlockType,
                eventExpressions,
                mainBlock
            )
        }

        private fun getActionBlockType(annotations: List<KtAnnotationFunction>, line: Line): ActionBlockType {
            if (annotations.isEmpty()) {
                throw LineException("action block annotations expected", line)
            }
            if (annotations.size > 1) {
                throw LineException("illegal action block type", line)
            }
            return when (annotations[0]) {
                KtAnnotationFunction.COM -> ActionBlockType.COM
                KtAnnotationFunction.SEQ -> ActionBlockType.SEQ
                KtAnnotationFunction.RUN -> ActionBlockType.RUN
                else -> throw LineException("illegal action block type", line)
            }
        }

        private fun getMainBlockAndEventExpressions(block: KtBlock, line: Line): Pair<VkBlock, List<VkExpression>> {
            val isOnExpression = { it: KtStatement ->
                it is KtStatementExpression
                        && it.expression is KtExpressionOperator
                        && it.expression.operatorSymbol == OPERATOR_ON
            }
            return if (block.statements.any { isOnExpression(it) }) {
                if (block.statements.size != 1) {
                    throw LineException("illegal use of on expression", line)
                }
                val statementExpression = block.statements[0] as KtStatementExpression
                val onExpression = statementExpression.expression as KtExpressionOperator
                val mainBlock = VkBlock(onExpression.blocks[0])
                val eventExpressions = onExpression.args.map { VkExpression(it) }
                Pair(mainBlock, eventExpressions)
            } else Pair(VkBlock(block), listOf())
        }
    }
}
