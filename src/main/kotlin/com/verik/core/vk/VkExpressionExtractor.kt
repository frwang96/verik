package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.*

// Copyright (c) 2020 Francis Wang

class VkExpressionExtractor {

    companion object {

        fun extract(expression: VkExpression): SvExpression {
            return when (expression) {
                is VkFunctionExpression -> extractFunctionExpression(expression)
                is VkNavigationExpression -> throw VkExtractException(expression.linePos, "navigation suffixes are not supported")
                is VkIdentifierExpression -> SvIdentifierExpression(expression.identifier)
                is VkLiteralExpression -> SvLiteralExpression(expression.value)
            }
        }

        private fun extractFunctionExpression(expression: VkFunctionExpression): SvExpression {
            val args = expression.args.map { extract(it) }
            return when (expression.functionType) {
                VkFunctionType.REGULAR -> throw VkExtractException(expression.linePos, "only operator functions are supported")
                VkFunctionType.TARGETED -> throw VkExtractException(expression.linePos, "only operator functions are supported")
                VkFunctionType.OPERATOR -> extractOperatorExpression(expression.name, args, expression.linePos)
            }
        }

        private fun extractOperatorExpression(name: String, args: List<SvExpression>, linePos: LinePos): SvExpression {
            return when (name) {
                "put" -> makeOperatorExpression("bassign", args[0], args[1])
                "add" -> makeOperatorExpression("add", args[0], args[1])
                "add_tru" -> makeOperatorExpression("add", args[0], args[1])
                "sub" -> makeOperatorExpression("sub", args[0], args[1])
                "sub_tru" -> makeOperatorExpression("sub", args[0], args[1])
                "mul" -> makeOperatorExpression("mul", args[0], args[1])
                "mul_tru" -> makeOperatorExpression("mul", args[0], args[1])
                else -> throw VkExtractException(linePos, "unsupported operator $name")
            }
        }

        private fun makeOperatorExpression(name: String, vararg args: SvExpression): SvExpression {
            return SvFunctionExpression(name, SvFunctionType.OPERATOR, args.toList())
        }
    }
}