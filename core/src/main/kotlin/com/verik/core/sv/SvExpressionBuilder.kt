package com.verik.core.sv

import com.verik.core.LinePos

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
                    } else throw SvBuildException("only operator functions are supported", expression.linePos)
                }
                is SvIdentifierExpression -> ExpressionString(expression.identifier, 0)
                is SvLiteralExpression -> ExpressionString(expression.value, 0)
            }
        }

        private fun buildOperatorExpressionString(expression: SvFunctionExpression): ExpressionString {
            val precedence = getOperatorPrecedence(expression.name, expression.linePos)
            val args = expression.args

            val string = when (expression.name) {
                "mul" -> "${wrapIfLess(args[0], precedence)} * ${wrapIfLessEq(args[1], precedence)}"
                "add" -> "${wrapIfLess(args[0], precedence)} + ${wrapIfLessEq(args[1], precedence)}"
                "sub" -> "${wrapIfLess(args[0], precedence)} - ${wrapIfLessEq(args[1], precedence)}"
                "and" -> "${wrapIfLess(args[0], precedence)} && ${wrapIfLessEq(args[1], precedence)}"
                "or" -> "${wrapIfLess(args[0], precedence)} || ${wrapIfLessEq(args[1], precedence)}"
                "bassign" -> "${wrapNone(args[0])} = ${wrapNone(args[1])}"
                else -> throw SvBuildException("unsupported operator ${expression.name}", expression.linePos)
            }

            return ExpressionString(string, precedence)
        }

        private fun getOperatorPrecedence(name: String, linePos: LinePos): Int {
            return when (name) {
                "mul" -> 3
                "add", "sub" -> 4
                "and" -> 11
                "or" -> 12
                "bassign" -> 15
                else -> throw SvBuildException("unsupported operator $name", linePos)
            }
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