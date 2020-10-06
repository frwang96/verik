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

package verik.core.sv.build

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.sv.*

object SvExpressionBuilder {

    fun build(expression: SvExpression): String {
        return buildExpressionString(expression).first
    }

    private fun buildExpressionString(expression: SvExpression): Pair<String, Int> {
        return when (expression) {
            is SvExpressionFunction -> buildExpressionFunctionString(expression)
            is SvExpressionOperator -> buildExpressionOperatorString(expression)
            is SvExpressionProperty -> buildExpressionPropertyString(expression)
            is SvExpressionLiteral -> Pair(expression.string, 0)
        }
    }

    private fun buildExpressionFunctionString(expression: SvExpressionFunction): Pair<String, Int> {
        val args = expression.args.map { buildExpressionString(it).first }
        val argString = args.joinToString()
        return if (expression.receiver != null) {
            val receiverString = buildExpressionString(expression.receiver).first
            Pair("$receiverString.${expression.identifier}($argString)", 0)
        } else {
            Pair("${expression.identifier}($argString)", 0)
        }
    }

    private fun buildExpressionOperatorString(expression: SvExpressionOperator): Pair<String, Int> {
        val receiver = expression.receiver
        val args = expression.args
        val precedence = expression.type.precedence()
        val wrapper = OperatorWrapper(precedence, expression)

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
            SvOperatorType.IF -> {
                "${wrapper.eager(receiver)} ? ${wrapper.eager(args[0])} : ${wrapper.eager(args[1])}"
            }
            SvOperatorType.BLOCK_ASSIGN -> {
                "${wrapper.eager(receiver)} = ${wrapper.eager(args[0])}"
            }
            SvOperatorType.NONBLOCK_ASSIGN -> {
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

    private fun buildExpressionPropertyString(expression: SvExpressionProperty): Pair<String, Int> {
        return if (expression.receiver != null) {
            val receiverString = buildExpressionString(expression.receiver).first
            Pair("$receiverString.${expression.identifier}", 0)
        } else {
            Pair(expression.identifier, 0)
        }
    }

    private class OperatorWrapper(val precedence: Int, val line: Line) {

        fun eager(expression: SvExpression?): String {
            if (expression == null) throw LineException("operator expression is null", line)
            val (string, precedence) = buildExpressionString(expression)
            return if (precedence >= this.precedence) "($string)"
            else string
        }

        fun lazy(expression: SvExpression?): String {
            if (expression == null) throw LineException("operator expression is null", line)
            val (string, precedence) = buildExpressionString(expression)
            return if (precedence > this.precedence) "($string)"
            else string
        }
    }
}