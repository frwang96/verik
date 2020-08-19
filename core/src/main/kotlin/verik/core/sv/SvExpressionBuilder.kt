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

package verik.core.sv

import verik.core.base.Line
import verik.core.base.LineException

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
        return if (expression.target != null) {
            val targetString = buildExpressionString(expression.target).first
            Pair("$targetString.${expression.identifier}($argString)", 0)
        } else {
            Pair("${expression.identifier}($argString)", 0)
        }
    }

    private fun buildExpressionOperatorString(expression: SvExpressionOperator): Pair<String, Int> {
        val target = expression.target
        val args = expression.args
        val precedence = expression.identifier.precedence()
        val wrapper = OperatorWrapper(precedence, expression)

        val string = when (expression.identifier) {
            SvOperatorIdentifier.ADD -> {
                "${wrapper.lazy(target)} + ${wrapper.eager(args[0])}"
            }
            SvOperatorIdentifier.SUB -> {
                "${wrapper.lazy(target)} - ${wrapper.eager(args[0])}"
            }
            SvOperatorIdentifier.MUL -> {
                "${wrapper.lazy(target)} * ${wrapper.eager(args[0])}"
            }
            SvOperatorIdentifier.IF -> {
                "${wrapper.eager(target)} ? ${wrapper.eager(args[0])} : ${wrapper.eager(args[1])}"
            }
            SvOperatorIdentifier.BLOCK_ASSIGN -> {
                "${wrapper.eager(target)} = ${wrapper.eager(args[0])}"
            }
            SvOperatorIdentifier.NBLOCK_ASSIGN -> {
                "${wrapper.eager(target)} <= ${wrapper.eager(args[0])}"
            }
        }

        return Pair(string, precedence)
    }

    private fun buildExpressionPropertyString(expression: SvExpressionProperty): Pair<String, Int> {
        return if (expression.target != null) {
            val targetString = buildExpressionString(expression.target).first
            Pair("$targetString.${expression.identifier}", 0)
        } else {
            Pair(expression.identifier, 0)
        }
    }

    private class OperatorWrapper(val precedence: Int, val line: Line) {

        fun eager(expression: SvExpression?): String {
            if (expression == null) throw LineException("expression target is null", line)
            val (string, precedence) = buildExpressionString(expression)
            return if (precedence >= this.precedence) "($string)"
            else string
        }

        fun lazy(expression: SvExpression?): String {
            if (expression == null) throw LineException("expression target is null", line)
            val (string, precedence) = buildExpressionString(expression)
            return if (precedence > this.precedence) "($string)"
            else string
        }
    }
}