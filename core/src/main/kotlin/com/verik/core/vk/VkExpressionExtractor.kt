package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.*

// Copyright (c) 2020 Francis Wang

class VkExpressionExtractor {

    companion object {

        fun extract(expression: VkExpression): SvExpression {
            return when (expression) {
                is VkFunctionExpression -> extractFunctionExpression(expression)
                is VkNavigationExpression -> throw VkExtractException("navigation suffixes are not supported", expression.linePos)
                is VkIdentifierExpression -> SvIdentifierExpression(expression.linePos, expression.identifier)
                is VkLiteralExpression -> SvLiteralExpression(expression.linePos, expression.value)
            }
        }

        private fun extractFunctionExpression(expression: VkFunctionExpression): SvExpression {
            val args = expression.args.map { extract(it) }
            return when (expression.functionType) {
                VkFunctionType.REGULAR -> throw VkExtractException("only operator functions are supported", expression.linePos)
                VkFunctionType.TARGETED -> throw VkExtractException("only operator functions are supported", expression.linePos)
                VkFunctionType.OPERATOR -> extractOperatorExpression(expression.identifier, args, expression.linePos)
            }
        }

        private fun extractOperatorExpression(identifier: String, args: List<SvExpression>, linePos: LinePos): SvExpression {
            return when (identifier) {
                "put" -> makeOperatorExpression(linePos, "bassign", args[0], args[1])
                "add" -> makeOperatorExpression(linePos, "add", args[0], args[1])
                "add_tru" -> makeOperatorExpression(linePos, "add", args[0], args[1])
                "sub" -> makeOperatorExpression(linePos, "sub", args[0], args[1])
                "sub_tru" -> makeOperatorExpression(linePos, "sub", args[0], args[1])
                "mul" -> makeOperatorExpression(linePos, "mul", args[0], args[1])
                "mul_tru" -> makeOperatorExpression(linePos, "mul", args[0], args[1])
                else -> throw VkExtractException("unsupported operator $identifier", linePos)
            }
        }

        private fun makeOperatorExpression(linePos: LinePos, identifier: String, vararg args: SvExpression): SvExpression {
            return SvFunctionExpression(linePos, identifier, SvFunctionType.OPERATOR, args.toList())
        }
    }
}