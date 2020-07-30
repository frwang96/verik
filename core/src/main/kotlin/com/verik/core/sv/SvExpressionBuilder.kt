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

package com.verik.core.sv

class SvExpressionBuilder {

    data class ExpressionString(val string: String, val precedence: Int)

    companion object {

        fun build(expression: SvExpression): String {
            return buildExpressionString(expression).string
        }

        private fun buildExpressionString(expression: SvExpression): ExpressionString {
            return when (expression) {
                is SvCallableExpression -> buildCallableExpressionString(expression)
                is SvOperatorExpression -> buildOperatorExpressionString(expression)
                is SvIdentifierExpression -> ExpressionString(expression.identifier, 0)
                is SvLiteralExpression -> ExpressionString(expression.value, 0)
                is SvStringExpression -> ExpressionString("\"${expression.string}\"", 0)
            }
        }

        private fun buildCallableExpressionString(expression: SvCallableExpression): ExpressionString {
            val target = buildExpressionString(expression.target)
            val args = expression.args.map { buildExpressionString(it) }
            return if (args.isEmpty()) target
            else {
                val argString = args.joinToString(prefix = "(", postfix = ")") { it.string }
                ExpressionString(target.string + argString, 0)
            }
        }

        private fun buildOperatorExpressionString(expression: SvOperatorExpression): ExpressionString {
            val precedence = expression.type.precedence()
            val args = expression.args

            val string = when (expression.type) {
                SvOperatorType.DELAY -> "#${wrapIfLess(args[0], precedence)}"
                SvOperatorType.NOT -> "!${wrapIfLess(args[0], precedence)}"
                SvOperatorType.MUL -> "${wrapIfLess(args[0], precedence)} * ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.ADD -> "${wrapIfLess(args[0], precedence)} + ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.SUB -> "${wrapIfLess(args[0], precedence)} - ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.AND -> "${wrapIfLess(args[0], precedence)} && ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.OR -> "${wrapIfLess(args[0], precedence)} || ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.IF -> "${wrapIfLessEq(args[0], precedence)} ? ${wrapIfLessEq(args[1], precedence)} : ${wrapIfLessEq(args[2], precedence)}"
                SvOperatorType.BASSIGN -> "${wrapIfLessEq(args[0], precedence)} = ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.NBASSIGN -> "${wrapIfLessEq(args[0], precedence)} <= ${wrapIfLessEq(args[1], precedence)}"
            }

            return ExpressionString(string, precedence)
        }

        private fun wrapIfLess(expression: SvExpression, precedence: Int): String {
            val expressionString = buildExpressionString(expression)
            return if (expressionString.precedence > precedence) "(${expressionString.string})"
            else expressionString.string
        }

        private fun wrapIfLessEq(expression: SvExpression, precedence: Int): String {
            val expressionString = buildExpressionString(expression)
            return if (expressionString.precedence >= precedence) "(${expressionString.string})"
            else expressionString.string
        }
    }
}