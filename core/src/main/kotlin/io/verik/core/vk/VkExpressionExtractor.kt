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

import io.verik.core.FileLineException
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
                            else throw FileLineException("unable to extract lambda expression", expression.fileLine)
                        }
                    } else throw FileLineException("unable to extract lambda expression", expression.fileLine)
                }
                is VkExpressionCallable -> {
                    extractExpressionCallable(expression)
                }
                is VkExpressionOperator -> {
                    extractExpressionOperator(expression)
                }
                is VkExpressionNavigation -> {
                    throw FileLineException("navigation suffixes are not supported", expression.fileLine)
                }
                is VkExpressionIdentifier -> {
                    SvExpressionIdentifier(expression.fileLine, expression.identifier)
                }
                is VkExpressionLiteral -> {
                    SvExpressionLiteral(expression.fileLine, expression.value)
                }
                is VkExpressionString -> {
                    VkStringExtractor.extract(expression)
                }
            }
        }

        private fun extractExpressionCallable(expression: VkExpressionCallable): SvExpression {
            val identifier = expression.target.let {
                if (it is VkExpressionIdentifier) it.identifier
                else throw FileLineException("only simple identifiers are supported in callable expressions", expression.fileLine)
            }

            return when (identifier) {
                "wait" -> SvExpressionOperator(expression.fileLine,
                        SvOperatorType.DELAY, listOf(extractExpression(expression.args[0])))
                "print" -> SvExpressionCallable(expression.fileLine,
                        SvExpressionLiteral(expression.fileLine, "\$write"), listOf(extractExpression(expression.args[0])))
                "println" -> SvExpressionCallable(expression.fileLine,
                        SvExpressionLiteral(expression.fileLine, "\$display"), listOf(extractExpression(expression.args[0])))
                "finish" -> SvExpressionCallable(expression.fileLine,
                        SvExpressionLiteral(expression.fileLine, "\$finish"), listOf())
                else -> throw FileLineException("callable $identifier not supported", expression.fileLine)
            }
        }

        private fun extractExpressionOperator(expression: VkExpressionOperator): SvExpression {
            val args = expression.args.map { extractExpression(it) }
            val fileLine = expression.fileLine
            return when (expression.type) {
                VkOperatorType.PUT -> SvExpressionOperator(fileLine, SvOperatorType.BASSIGN, listOf(args[0], args[1]))
                VkOperatorType.REG -> SvExpressionOperator(fileLine, SvOperatorType.NBASSIGN, listOf(args[0], args[1]))
                VkOperatorType.NOT -> SvExpressionOperator(fileLine, SvOperatorType.NOT, listOf(args[0]))
                VkOperatorType.ADD_TRU -> SvExpressionOperator(fileLine, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.ADD -> SvExpressionOperator(fileLine, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.SUB_TRU -> SvExpressionOperator(fileLine, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.SUB -> SvExpressionOperator(fileLine, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.MUL_TRU -> SvExpressionOperator(fileLine, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.MUL -> SvExpressionOperator(fileLine, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.IF_ELSE -> SvExpressionOperator(fileLine, SvOperatorType.IF, listOf(args[0], args[1], args[2]))
                else -> throw FileLineException("unsupported operator ${expression.type}", fileLine)
            }
        }
    }
}