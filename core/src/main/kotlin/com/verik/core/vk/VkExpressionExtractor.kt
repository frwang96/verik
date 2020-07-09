package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.*

// Copyright (c) 2020 Francis Wang

class VkExpressionExtractor {

    companion object {

        fun extract(expression: VkExpression): SvExpression {
            return when (expression) {
                is VkLambdaExpression -> throw VkExtractException("lambda expressions should not be extracted", expression.linePos)
                is VkCallableExpression -> throw VkExtractException("callable expressions are not supported", expression.linePos)
                is VkOperatorExpression -> extractOperatorExpression(expression)
                is VkNavigationExpression -> throw VkExtractException("navigation suffixes are not supported", expression.linePos)
                is VkIdentifierExpression -> SvIdentifierExpression(expression.linePos, expression.identifier)
                is VkLiteralExpression -> SvLiteralExpression(expression.linePos, expression.value)
            }
        }

        private fun extractOperatorExpression(expression: VkOperatorExpression): SvExpression {
            val args = expression.args.map { extract(it) }
            val linePos = expression.linePos
            return when (expression.type) {
                VkOperatorType.PUT -> SvOperatorExpression(linePos, SvOperatorType.BASSIGN, listOf(args[0], args[1]))
                VkOperatorType.ADD_TRU -> SvOperatorExpression(linePos, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.ADD -> SvOperatorExpression(linePos, SvOperatorType.ADD, listOf(args[0], args[1]))
                VkOperatorType.SUB_TRU -> SvOperatorExpression(linePos, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.SUB -> SvOperatorExpression(linePos, SvOperatorType.SUB, listOf(args[0], args[1]))
                VkOperatorType.MUL_TRU -> SvOperatorExpression(linePos, SvOperatorType.MUL, listOf(args[0], args[1]))
                VkOperatorType.MUL -> SvOperatorExpression(linePos, SvOperatorType.MUL, listOf(args[0], args[1]))
                else -> throw VkExtractException("unsupported operator ${expression.type}", linePos)
            }
        }
    }
}