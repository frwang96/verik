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

package io.verik.core.sv

object SvExpressionBuilder {

    fun build(expression: SvExpression): String {
        return buildExpressionString(expression).first
    }

    private fun buildExpressionString(expression: SvExpression): Pair<String, Int> {
        return when (expression) {
            is SvExpressionCallable -> buildExpressionCallableString(expression)
            is SvExpressionOperator -> buildExpressionOperatorString(expression)
            is SvExpressionIdentifier -> Pair(expression.identifier, 0)
            is SvExpressionLiteral -> Pair(expression.value, 0)
            is SvExpressionString -> Pair("\"${expression.string}\"", 0)
        }
    }

    private fun buildExpressionCallableString(expression: SvExpressionCallable): Pair<String, Int> {
        val target = buildExpressionString(expression.target)
        val args = expression.args.map { buildExpressionString(it) }
        return if (args.isEmpty()) target
        else {
            val argString = args.joinToString(prefix = "(", postfix = ")") { it.first }
            Pair(target.first + argString, 0)
        }
    }

    private fun buildExpressionOperatorString(expression: SvExpressionOperator): Pair<String, Int> {
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

        return Pair(string, precedence)
    }

    private fun wrapIfLess(expression: SvExpression, precedence: Int): String {
        val (expressionString, expressionPrecedence) = buildExpressionString(expression)
        return if (expressionPrecedence > precedence) "($expressionString)"
        else expressionString
    }

    private fun wrapIfLessEq(expression: SvExpression, precedence: Int): String {
        val (expressionString, expressionPrecedence) = buildExpressionString(expression)
        return if (expressionPrecedence >= precedence) "($expressionString)"
        else expressionString
    }
}