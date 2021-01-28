/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.vk.build

import verikc.base.ast.ActionBlockType
import verikc.base.ast.AnnotationFunction
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.lang.LangSymbol.OPERATOR_ON
import verikc.rs.ast.*
import verikc.vk.ast.VkActionBlock
import verikc.vk.ast.VkBlock
import verikc.vk.ast.VkExpression

object VkBuilderActionBlock {

    fun match(function: RsFunction): Boolean {
        return function.annotations.any {
            it in listOf(
                AnnotationFunction.COM,
                AnnotationFunction.SEQ,
                AnnotationFunction.RUN
            )
        }
    }

    fun build(function: RsFunction): VkActionBlock {
        val actionBlockType = getActionBlockType(function.annotations, function.line)
        val (mainBlock, eventExpressions) = getMainBlockAndEventExpressions(function.getBlockNotNull(), function.line)

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

    private fun getActionBlockType(annotations: List<AnnotationFunction>, line: Line): ActionBlockType {
        if (annotations.isEmpty()) {
            throw LineException("action block annotations expected", line)
        }
        if (annotations.size > 1) {
            throw LineException("illegal action block type", line)
        }
        return when (annotations[0]) {
            AnnotationFunction.COM -> ActionBlockType.COM
            AnnotationFunction.SEQ -> ActionBlockType.SEQ
            AnnotationFunction.RUN -> ActionBlockType.RUN
            else -> throw LineException("illegal action block type", line)
        }
    }

    private fun getMainBlockAndEventExpressions(block: RsBlock, line: Line): Pair<VkBlock, List<VkExpression>> {
        val isOnExpression = { it: RsStatement ->
            it is RsStatementExpression
                    && it.expression is RsExpressionOperator
                    && it.expression.operatorSymbol == OPERATOR_ON
        }
        return if (block.statements.any { isOnExpression(it) }) {
            if (block.statements.size != 1) {
                throw LineException("illegal use of on expression", line)
            }
            val statementExpression = block.statements[0] as RsStatementExpression
            val onExpression = statementExpression.expression as RsExpressionOperator
            val mainBlock = VkBuilderBlock.build(onExpression.blocks[0])
            val eventExpressions = onExpression.args.map { VkExpression(it) }
            Pair(mainBlock, eventExpressions)
        } else Pair(VkBuilderBlock.build(block), listOf())
    }
}
