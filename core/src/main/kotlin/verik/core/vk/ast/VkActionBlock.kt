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

package verik.core.vk.ast

import verik.core.base.ast.ActionBlockType
import verik.core.base.ast.Line
import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.kt.ast.*
import verik.core.lang.LangSymbol.OPERATOR_ON

data class VkActionBlock(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val actionBlockType: ActionBlockType,
        val eventExpressions: List<VkExpression>,
        val block: VkBlock
): VkDeclaration {

    companion object {

        fun isActionBlock(declaration: KtDeclaration): Boolean {
            return declaration is KtPrimaryFunction
                    && declaration.body is KtFunctionBodyBlock
                    && declaration.annotations.any {
                it in listOf(
                        KtAnnotationFunction.COM,
                        KtAnnotationFunction.SEQ,
                        KtAnnotationFunction.RUN
                )
            }
        }

        operator fun invoke(declaration: KtDeclaration): VkActionBlock {
            val primaryFunction = declaration.let {
                if (it is KtPrimaryFunction) it
                else throw LineException("function declaration expected", it)
            }

            if (primaryFunction.body !is KtFunctionBodyBlock) {
                throw LineException("block expected for function body", primaryFunction)
            }

            val actionBlockType = getActionBlockType(primaryFunction.annotations, primaryFunction.line)
            val (block, eventExpressions) = getBlockAndEventExpressions(primaryFunction.body, primaryFunction)

            if (actionBlockType == ActionBlockType.SEQ) {
                if (eventExpressions.isEmpty()) {
                    throw LineException("on expression expected for seq block", primaryFunction)
                }
            } else {
                if (eventExpressions.isNotEmpty()) {
                    throw LineException("on expression not permitted here", primaryFunction)
                }
            }

            return VkActionBlock(
                    primaryFunction.line,
                    primaryFunction.identifier,
                    primaryFunction.symbol,
                    actionBlockType,
                    eventExpressions,
                    block
            )
        }

        private fun getActionBlockType(annotations: List<KtAnnotationFunction>, line: Int): ActionBlockType {
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
                KtAnnotationFunction.TASK -> throw LineException("illegal action block type", line)
            }
        }

        private fun getBlockAndEventExpressions(body: KtFunctionBodyBlock, line: Line): Pair<VkBlock, List<VkExpression>> {
            val isOnExpression = { it: KtStatement ->
                it is KtStatementExpression
                        && it.expression is KtExpressionOperator
                        && it.expression.operator == OPERATOR_ON
            }
            return if (body.block.statements.any { isOnExpression(it) }) {
                if (body.block.statements.size != 1) {
                    throw LineException("illegal use of on expression", line)
                }
                val statementExpression = body.block.statements[0] as KtStatementExpression
                val onExpression = statementExpression.expression as KtExpressionOperator
                val block = VkBlock(onExpression.blocks[0])
                val eventExpressions = onExpression.args.map { VkExpression(it) }
                Pair(block, eventExpressions)
            } else Pair(VkBlock(body.block), listOf())
        }
    }
}
