package com.verik.core.vk

import com.verik.core.sv.*

// Copyright (c) 2020 Francis Wang

class VkExpressionExtractor {

    companion object {

        fun extract(expression: VkExpression): SvExpression {
            return when (expression) {
                is VkLambdaExpression -> throw VkExtractException("lambda expressions should not be extracted", expression.linePos)
                is VkCallableExpression -> extractCallableExpression(expression)
                is VkOperatorExpression -> extractOperatorExpression(expression)
                is VkNavigationExpression -> throw VkExtractException("navigation suffixes are not supported", expression.linePos)
                is VkIdentifierExpression -> SvIdentifierExpression(expression.linePos, expression.identifier)
                is VkLiteralExpression -> SvLiteralExpression(expression.linePos, expression.value)
                is VkStringExpression -> VkStringExtractor.extract(expression)
            }
        }

        private fun extractCallableExpression(expression: VkCallableExpression): SvExpression {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw VkExtractException("only simple identifiers are supported in callable expressions", expression.linePos)
            }

            return when (identifier) {
                "vk_wait" -> SvOperatorExpression(expression.target.linePos, SvOperatorType.DELAY, listOf(expression.args[0].extract()))
                "vk_print" -> SvCallableExpression(expression.target.linePos,
                        SvLiteralExpression(expression.target.linePos, "\$write"), listOf(expression.args[0].extract()))
                "vk_println" -> SvCallableExpression(expression.target.linePos,
                        SvLiteralExpression(expression.target.linePos, "\$display"), listOf(expression.args[0].extract()))
                "vk_finish" -> SvCallableExpression(expression.target.linePos,
                        SvLiteralExpression(expression.target.linePos, "\$finish"), listOf())
                else -> throw VkExtractException("callable $identifier not supported", expression.linePos)
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