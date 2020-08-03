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
                is VkLambdaExpression -> {
                    if (expression.statements.size == 1) {
                        val statement = expression.statements[0]
                        statement.extract().let {
                            if (it is SvExpressionStatement) it.expression
                            else throw FileLineException("unable to extract lambda expression", expression.fileLine)
                        }
                    } else throw FileLineException("unable to extract lambda expression", expression.fileLine)
                }
                is VkCallableExpression -> {
                    extractCallableExpression(expression)
                }
                is VkOperatorExpression -> {
                    extractOperatorExpression(expression)
                }
                is VkNavigationExpression -> {
                    throw FileLineException("navigation suffixes are not supported", expression.fileLine)
                }
                is VkIdentifierExpression -> {
                    SvIdentifierExpression(expression.fileLine, expression.identifier)
                }
                is VkLiteralExpression -> {
                    SvLiteralExpression(expression.fileLine, expression.value)
                }
                is VkStringExpression -> {
                    VkStringExtractor.extract(expression)
                }
            }
        }

        private fun extractCallableExpression(expression: VkCallableExpression): SvExpression {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw FileLineException("only simple identifiers are supported in callable expressions", expression.fileLine)
            }

            return when (identifier) {
                "wait" -> SvOperatorExpression(expression.fileLine,
                        SvOperatorType.DELAY, listOf(extractExpression(expression.args[0])))
                "print" -> SvCallableExpression(expression.fileLine,
                        SvLiteralExpression(expression.fileLine, "\$write"), listOf(extractExpression(expression.args[0])))
                "println" -> SvCallableExpression(expression.fileLine,
                        SvLiteralExpression(expression.fileLine, "\$display"), listOf(extractExpression(expression.args[0])))
                "finish" -> SvCallableExpression(expression.fileLine,
                        SvLiteralExpression(expression.fileLine, "\$finish"), listOf())
                else -> throw FileLineException("callable $identifier not supported", expression.fileLine)
            }
        }

        private fun extractOperatorExpression(expression: VkOperatorExpression): SvExpression {
            val args = expression.args.map { extractExpression(it) }
            val fileLine = expression.fileLine
            return when (expression.type) {
                VkOperatorType.PUT -> SvOperatorExpression(fileLine, SvOperatorType.BASSIGN, listOf(args[0], args[1]))
                VkOperatorType.REG -> SvOperatorExpression(fileLine, SvOperatorType.NBASSIGN, listOf(args[0], args[1]))
                VkOperatorType.NOT -> SvOperatorExpression(fileLine, SvOperatorType.NOT, listOf(args[0]))
                VkOperatorType.ADD_TRU -> SvOperatorExpression(fileLine, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.ADD -> SvOperatorExpression(fileLine, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.SUB_TRU -> SvOperatorExpression(fileLine, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.SUB -> SvOperatorExpression(fileLine, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.MUL_TRU -> SvOperatorExpression(fileLine, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.MUL -> SvOperatorExpression(fileLine, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.IF_ELSE -> SvOperatorExpression(fileLine, SvOperatorType.IF, listOf(args[0], args[1], args[2]))
                else -> throw FileLineException("unsupported operator ${expression.type}", fileLine)
            }
        }
    }
}