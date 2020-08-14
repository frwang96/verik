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

import verik.core.main.LineException
import verik.core.sv.SvConditionalStatement
import verik.core.sv.SvExpressionStatement
import verik.core.sv.SvLoopStatement
import verik.core.sv.SvStatement

object VkStatementExtractor {

    fun extractStatement(expression: VkExpression): SvStatement {
        val statement = when (expression) {
            is VkExpressionCallable -> {
                extractExpressionCallable(expression)
            }
            is VkExpressionOperator -> {
                extractExpressionOperator(expression)
            }
            else -> null
        }
        return statement ?: SvExpressionStatement(expression.line, VkExpressionExtractor.extractExpression(expression))
    }

    private fun extractExpressionCallable(expression: VkExpressionCallable): SvStatement? {
        val identifier = expression.target.let {
            if (it is VkExpressionIdentifier) it.identifier
            else throw LineException("only simple identifiers are supported in callable expressions", expression)
        }

        return when (identifier) {
            "forever" -> SvLoopStatement(expression.line, "forever", extractExpressionLambda(expression.args[0]))
            else -> null
        }
    }

    private fun extractExpressionOperator(expression: VkExpressionOperator): SvStatement? {
        return when (expression.type) {
            VkOperatorType.IF -> {
                val ifExpression = expression.args[0].extractExpression()
                val ifBody = extractExpressionLambda(expression.args[1])
                SvConditionalStatement(expression.line, ifExpression, ifBody, listOf())
            }
            VkOperatorType.IF_ELSE -> {
                val ifExpression = expression.args[0].extractExpression()
                val ifBody = extractExpressionLambda(expression.args[1])
                val elseBody = extractExpressionLambda(expression.args[2])
                SvConditionalStatement(expression.line, ifExpression, ifBody, elseBody)
            }
            else -> null
        }
    }

    private fun extractExpressionLambda(expression: VkExpression): List<SvStatement> {
        return if (expression is VkExpressionLambda) {
            expression.statements.map { it.extract() }
        } else throw LineException("lambda expression expected", expression)
    }
}