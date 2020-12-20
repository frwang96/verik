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

package verikc.sv.build

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.sv.ast.*

object SvSimpleExpressionBuilder {

    fun build(expression: SvExpression): String {
        return buildWithPrecedence(expression).first
    }

    private fun buildWithPrecedence(expression: SvExpression): Pair<String, Int> {
        return when (expression) {
            is SvExpressionControlBlock -> throw LineException("unexpected expression control block", expression.line)
            is SvExpressionOperator -> buildOperator(expression)
            is SvExpressionFunction -> buildFunction(expression)
            is SvExpressionProperty -> buildProperty(expression)
            is SvExpressionLiteral -> Pair(expression.string, 0)
        }
    }

    private fun buildOperator(expression: SvExpressionOperator): Pair<String, Int> {
        val receiver = expression.receiver
        val args = expression.args
        val precedence = expression.type.precedence()
        val wrapper = OperatorWrapper(precedence, expression.line)

        val string = when (expression.type) {
            SvOperatorType.NOT -> {
                "!${wrapper.eager(receiver)}"
            }
            SvOperatorType.ADD -> {
                "${wrapper.lazy(receiver)} + ${wrapper.eager(args[0])}"
            }
            SvOperatorType.SUB -> {
                "${wrapper.lazy(receiver)} - ${wrapper.eager(args[0])}"
            }
            SvOperatorType.MUL -> {
                "${wrapper.lazy(receiver)} * ${wrapper.eager(args[0])}"
            }
            SvOperatorType.EQUALITY -> {
                "${wrapper.lazy(receiver)} == ${wrapper.eager(args[0])}"
            }
            SvOperatorType.IF -> {
                "${wrapper.eager(receiver)} ? ${wrapper.eager(args[0])} : ${wrapper.eager(args[1])}"
            }
            SvOperatorType.ASSIGN_BLOCKING -> {
                "${wrapper.eager(receiver)} = ${wrapper.eager(args[0])}"
            }
            SvOperatorType.ASSIGN_NONBLOCKING -> {
                "${wrapper.eager(receiver)} <= ${wrapper.eager(args[0])}"
            }
            SvOperatorType.DELAY -> {
                "#${wrapper.eager(args[0])}"
            }
            SvOperatorType.POSEDGE -> {
                "posedge ${wrapper.eager(args[0])}"
            }
            SvOperatorType.NEGEDGE -> {
                "negedge ${wrapper.eager(args[0])}"
            }
        }

        return Pair(string, precedence)
    }

    private fun buildFunction(expression: SvExpressionFunction): Pair<String, Int> {
        val args = expression.args.map { buildWithPrecedence(it).first }
        val argString = args.joinToString()
        return if (expression.receiver != null) {
            val receiverString = buildWithPrecedence(expression.receiver).first
            Pair("$receiverString.${expression.identifier}($argString)", 0)
        } else {
            Pair("${expression.identifier}($argString)", 0)
        }
    }

    private fun buildProperty(expression: SvExpressionProperty): Pair<String, Int> {
        return if (expression.receiver != null) {
            val receiverString = buildWithPrecedence(expression.receiver).first
            Pair("$receiverString.${expression.identifier}", 0)
        } else {
            Pair(expression.identifier, 0)
        }
    }

    private class OperatorWrapper(val precedence: Int, val line: Line) {

        fun eager(expression: SvExpression?): String {
            if (expression == null) throw LineException("operator expression is null", line)
            val (string, precedence) = buildWithPrecedence(expression)
            return if (precedence >= this.precedence) "($string)"
            else string
        }

        fun lazy(expression: SvExpression?): String {
            if (expression == null) throw LineException("operator expression is null", line)
            val (string, precedence) = buildWithPrecedence(expression)
            return if (precedence > this.precedence) "($string)"
            else string
        }
    }
}
