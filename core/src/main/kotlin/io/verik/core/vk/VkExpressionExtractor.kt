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

import io.verik.core.LineException
import io.verik.core.sv.*

class VkExpressionExtractor {

    companion object {

        fun extractExpression(expression: VkExpression): SvExpression {
            return when (expression) {
                is VkExpressionLambda -> {
                    if (expression.statements.size == 1) {
                        val statement = expression.statements[0]
                        statement.extract().let {
                            if (it is SvExpressionStatement) it.expression
                            else throw LineException("unable to extract lambda expression", expression)
                        }
                    } else throw LineException("unable to extract lambda expression", expression)
                }
                is VkExpressionCallable -> {
                    extractExpressionCallable(expression)
                }
                is VkExpressionOperator -> {
                    extractExpressionOperator(expression)
                }
                is VkExpressionNavigation -> {
                    throw LineException("navigation suffixes are not supported", expression)
                }
                is VkExpressionIdentifier -> {
                    SvExpressionIdentifier(expression.line, expression.identifier)
                }
                is VkExpressionLiteral -> {
                    SvExpressionLiteral(expression.line, expression.value)
                }
                is VkExpressionString -> {
                    VkStringExtractor.extract(expression)
                }
            }
        }

        private fun extractExpressionCallable(expression: VkExpressionCallable): SvExpression {
            val identifier = expression.target.let {
                if (it is VkExpressionIdentifier) it.identifier
                else throw LineException("only simple identifiers are supported in callable expressions", expression)
            }

            return when (identifier) {
                "wait" -> SvExpressionOperator(expression.line,
                        SvOperatorType.DELAY, listOf(extractExpression(expression.args[0])))
                "print" -> SvExpressionCallable(expression.line,
                        SvExpressionLiteral(expression.line, "\$write"), listOf(extractExpression(expression.args[0])))
                "println" -> SvExpressionCallable(expression.line,
                        SvExpressionLiteral(expression.line, "\$display"), listOf(extractExpression(expression.args[0])))
                "finish" -> SvExpressionCallable(expression.line,
                        SvExpressionLiteral(expression.line, "\$finish"), listOf())
                else -> throw LineException("callable $identifier not supported", expression)
            }
        }

        private fun extractExpressionOperator(expression: VkExpressionOperator): SvExpression {
            val args = expression.args.map { extractExpression(it) }
            val line = expression.line
            return when (expression.type) {
                VkOperatorType.PUT -> SvExpressionOperator(line, SvOperatorType.BASSIGN, listOf(args[0], args[1]))
                VkOperatorType.REG -> SvExpressionOperator(line, SvOperatorType.NBASSIGN, listOf(args[0], args[1]))
                VkOperatorType.NOT -> SvExpressionOperator(line, SvOperatorType.NOT, listOf(args[0]))
                VkOperatorType.ADD_TRU -> SvExpressionOperator(line, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.ADD -> SvExpressionOperator(line, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.SUB_TRU -> SvExpressionOperator(line, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.SUB -> SvExpressionOperator(line, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.MUL_TRU -> SvExpressionOperator(line, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.MUL -> SvExpressionOperator(line, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.IF_ELSE -> SvExpressionOperator(line, SvOperatorType.IF, listOf(args[0], args[1], args[2]))
                else -> throw LineException("unsupported operator ${expression.type}", line)
            }
        }
    }
}