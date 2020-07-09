package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

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
                SvOperatorType.DELAY -> "#${wrapNone(args[0])}"
                SvOperatorType.MUL -> "${wrapIfLess(args[0], precedence)} * ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.ADD -> "${wrapIfLess(args[0], precedence)} + ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.SUB -> "${wrapIfLess(args[0], precedence)} - ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.AND -> "${wrapIfLess(args[0], precedence)} && ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.OR -> "${wrapIfLess(args[0], precedence)} || ${wrapIfLessEq(args[1], precedence)}"
                SvOperatorType.BASSIGN -> "${wrapNone(args[0])} = ${wrapNone(args[1])}"
            }

            return ExpressionString(string, precedence)
        }

        private fun wrapNone(expression: SvExpression): String {
            return buildExpressionString(expression).string
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