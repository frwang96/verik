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

import io.verik.core.LinePosException
import io.verik.core.sv.SvConditionalStatement
import io.verik.core.sv.SvExpressionStatement
import io.verik.core.sv.SvLoopStatement
import io.verik.core.sv.SvStatement

class VkStatementExtractor {

    companion object {

        fun extractStatement(expression: VkExpression): SvStatement {
            val statement = when (expression) {
                is VkCallableExpression -> {
                    extractCallableExpression(expression)
                }
                is VkOperatorExpression -> {
                    extractOperatorExpression(expression)
                }
                else -> null
            }
            return statement ?: SvExpressionStatement(expression.linePos, VkExpressionExtractor.extractExpression(expression))
        }

        private fun extractCallableExpression(expression: VkCallableExpression): SvStatement? {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw LinePosException("only simple identifiers are supported in callable expressions", expression.linePos)
            }

            return when (identifier) {
                "forever" -> SvLoopStatement(expression.linePos, "forever", extractLambdaExpression(expression.args[0]))
                else -> null
            }
        }

        private fun extractOperatorExpression(expression: VkOperatorExpression): SvStatement? {
            return when (expression.type) {
                VkOperatorType.IF -> {
                    val ifExpression = expression.args[0].extractExpression()
                    val ifBody = extractLambdaExpression(expression.args[1])
                    SvConditionalStatement(expression.linePos, ifExpression, ifBody, listOf())
                }
                VkOperatorType.IF_ELSE -> {
                    val ifExpression = expression.args[0].extractExpression()
                    val ifBody = extractLambdaExpression(expression.args[1])
                    val elseBody = extractLambdaExpression(expression.args[2])
                    SvConditionalStatement(expression.linePos, ifExpression, ifBody, elseBody)
                }
                else -> null
            }
        }

        private fun extractLambdaExpression(expression: VkExpression): List<SvStatement> {
            return if (expression is VkLambdaExpression) {
                expression.statements.map { it.extract() }
            } else throw LinePosException("lambda expression expected", expression.linePos)
        }
    }
}