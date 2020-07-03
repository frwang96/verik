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
                is SvFunctionExpression -> {
                    if (expression.functionType == SvFunctionType.OPERATOR) {
                        return buildOperatorExpressionString(expression)
                    } else throw SvAssertionException("only operator functions are supported")
                }
                is SvIdentifierExpression -> ExpressionString(expression.identifier, 0)
                is SvLiteralExpression -> ExpressionString(expression.value, 0)
            }
        }

        private fun buildOperatorExpressionString(expression: SvFunctionExpression): ExpressionString {
            val precedence = getOperatorPrecedence(expression.name)
            val args = expression.args

            val string = when (expression.name) {
                "mul" -> "${wrapLt(args[0], precedence)} * ${wrapLe(args[1], precedence)}"
                "add" -> "${wrapLt(args[0], precedence)} + ${wrapLe(args[1], precedence)}"
                "sub" -> "${wrapLt(args[0], precedence)} - ${wrapLe(args[1], precedence)}"
                else -> throw SvAssertionException("unsupported operator ${expression.name}")
            }

            return ExpressionString(string, precedence)
        }

        private fun getOperatorPrecedence(name: String): Int {
            return when (name) {
                "mul" -> 3
                "add", "sub" -> 4
                else -> throw SvAssertionException("unsupported operator $name")
            }
        }

        private fun wrapLt(expression: SvExpression, precedence: Int): String {
            val expressionString = buildExpressionString(expression)
            return if (expressionString.precedence > precedence) "(${expressionString.string})"
            else expressionString.string
        }

        private fun wrapLe(expression: SvExpression, precedence: Int): String {
            val expressionString = buildExpressionString(expression)
            return if (expressionString.precedence >= precedence) "(${expressionString.string})"
            else expressionString.string
        }
    }
}