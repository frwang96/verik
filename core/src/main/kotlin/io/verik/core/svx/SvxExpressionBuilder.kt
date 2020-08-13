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

package io.verik.core.svx

import io.verik.core.main.Line
import io.verik.core.main.LineException

object SvxExpressionBuilder {

    fun build(expression: SvxExpression): String {
        return buildExpressionString(expression).first
    }

    private fun buildExpressionString(expression: SvxExpression): Pair<String, Int> {
        return when (expression) {
            is SvxExpressionFunction -> buildExpressionFunctionString(expression)
            is SvxExpressionOperator -> buildExpressionOperatorString(expression)
            is SvxExpressionProperty -> buildExpressionPropertyString(expression)
            is SvxExpressionLiteral -> Pair(expression.string, 0)
        }
    }

    private fun buildExpressionFunctionString(expression: SvxExpressionFunction): Pair<String, Int> {
        val args = expression.args.map { buildExpressionString(it).first }
        val argString = args.joinToString()
        return if (expression.target != null) {
            val targetString = buildExpressionString(expression.target).first
            Pair("$targetString.${expression.identifier}($argString)", 0)
        } else {
            Pair("${expression.identifier}($argString)", 0)
        }
    }

    private fun buildExpressionOperatorString(expression: SvxExpressionOperator): Pair<String, Int> {
        val target = expression.target
        val args = expression.args
        val precedence = expression.identifier.precedence()
        val wrapper = OperatorWrapper(precedence, expression)

        val string = when (expression.identifier) {
            SvxOperatorIdentifier.ADD -> {
                "${wrapper.lazy(target)} + ${wrapper.eager(args[0])}"
            }
            SvxOperatorIdentifier.SUB -> {
                "${wrapper.lazy(target)} - ${wrapper.eager(args[0])}"
            }
            SvxOperatorIdentifier.MUL -> {
                "${wrapper.lazy(target)} * ${wrapper.eager(args[0])}"
            }
            SvxOperatorIdentifier.IF -> {
                "${wrapper.eager(target)} ? ${wrapper.eager(args[0])} : ${wrapper.eager(args[1])}"
            }
            SvxOperatorIdentifier.BLOCK_ASSIGN -> {
                "${wrapper.eager(target)} = ${wrapper.eager(args[0])}"
            }
            SvxOperatorIdentifier.NBLOCK_ASSIGN -> {
                "${wrapper.eager(target)} <= ${wrapper.eager(args[0])}"
            }
        }

        return Pair(string, precedence)
    }

    private fun buildExpressionPropertyString(expression: SvxExpressionProperty): Pair<String, Int> {
        return if (expression.target != null) {
            val targetString = buildExpressionString(expression.target).first
            Pair("$targetString.${expression.identifier}", 0)
        } else {
            Pair(expression.identifier, 0)
        }
    }

    private class OperatorWrapper(val precedence: Int, val line: Line) {

        fun eager(expression: SvxExpression?): String {
            if (expression == null) throw LineException("expression target is null", line)
            val (string, precedence) = buildExpressionString(expression)
            return if (precedence >= this.precedence) "($string)"
            else string
        }

        fun lazy(expression: SvxExpression?): String {
            if (expression == null) throw LineException("expression target is null", line)
            val (string, precedence) = buildExpressionString(expression)
            return if (precedence > this.precedence) "($string)"
            else string
        }
    }
}