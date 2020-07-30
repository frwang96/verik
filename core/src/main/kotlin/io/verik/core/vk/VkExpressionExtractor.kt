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
                            else throw LinePosException("unable to extract lambda expression", expression.linePos)
                        }
                    } else throw LinePosException("unable to extract lambda expression", expression.linePos)
                }
                is VkCallableExpression -> {
                    extractCallableExpression(expression)
                }
                is VkOperatorExpression -> {
                    extractOperatorExpression(expression)
                }
                is VkNavigationExpression -> {
                    throw LinePosException("navigation suffixes are not supported", expression.linePos)
                }
                is VkIdentifierExpression -> {
                    SvIdentifierExpression(expression.linePos, expression.identifier)
                }
                is VkLiteralExpression -> {
                    SvLiteralExpression(expression.linePos, expression.value)
                }
                is VkStringExpression -> {
                    VkStringExtractor.extract(expression)
                }
            }
        }

        private fun extractCallableExpression(expression: VkCallableExpression): SvExpression {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw LinePosException("only simple identifiers are supported in callable expressions", expression.linePos)
            }

            return when (identifier) {
                "wait" -> SvOperatorExpression(expression.linePos,
                        SvOperatorType.DELAY, listOf(extractExpression(expression.args[0])))
                "print" -> SvCallableExpression(expression.linePos,
                        SvLiteralExpression(expression.linePos, "\$write"), listOf(extractExpression(expression.args[0])))
                "println" -> SvCallableExpression(expression.linePos,
                        SvLiteralExpression(expression.linePos, "\$display"), listOf(extractExpression(expression.args[0])))
                "finish" -> SvCallableExpression(expression.linePos,
                        SvLiteralExpression(expression.linePos, "\$finish"), listOf())
                else -> throw LinePosException("callable $identifier not supported", expression.linePos)
            }
        }

        private fun extractOperatorExpression(expression: VkOperatorExpression): SvExpression {
            val args = expression.args.map { extractExpression(it) }
            val linePos = expression.linePos
            return when (expression.type) {
                VkOperatorType.PUT -> SvOperatorExpression(linePos, SvOperatorType.BASSIGN, listOf(args[0], args[1]))
                VkOperatorType.REG -> SvOperatorExpression(linePos, SvOperatorType.NBASSIGN, listOf(args[0], args[1]))
                VkOperatorType.NOT -> SvOperatorExpression(linePos, SvOperatorType.NOT, listOf(args[0]))
                VkOperatorType.ADD_TRU -> SvOperatorExpression(linePos, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.ADD -> SvOperatorExpression(linePos, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.SUB_TRU -> SvOperatorExpression(linePos, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.SUB -> SvOperatorExpression(linePos, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.MUL_TRU -> SvOperatorExpression(linePos, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.MUL -> SvOperatorExpression(linePos, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.IF_ELSE -> SvOperatorExpression(linePos, SvOperatorType.IF, listOf(args[0], args[1], args[2]))
                else -> throw LinePosException("unsupported operator ${expression.type}", linePos)
            }
        }
    }
}