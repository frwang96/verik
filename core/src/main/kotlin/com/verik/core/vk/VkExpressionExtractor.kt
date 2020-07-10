package com.verik.core.vk

import com.verik.core.sv.*

// Copyright (c) 2020 Francis Wang

class VkExpressionExtractor {

    companion object {

        fun extractStatement(expression: VkExpression): SvStatement {
            return when (expression) {
                is VkCallableExpression -> {
                    extractCallableExpressionAsStatement(expression)
                }
                else -> {
                    SvExpressionStatement(expression.linePos, extractExpression(expression))
                }
            }
        }

        private fun extractCallableExpressionAsStatement(expression: VkCallableExpression): SvStatement {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw VkExtractException("only simple identifiers are supported in callable expressions", expression.linePos)
            }

            return when (identifier) {
                "forever" -> SvLoopStatement(expression.linePos, "forever", extractLambdaExpression(expression.args[0]))
                else -> SvExpressionStatement(expression.linePos, extractCallableExpressionAsExpression(expression))
            }
        }

        private fun extractLambdaExpression(expression: VkExpression): List<SvStatement> {
            return if (expression is VkLambdaExpression) {
                expression.statements.map { it.extract() }
            } else throw VkExtractException("lambda expression expected", expression.linePos)
        }

        fun extractExpression(expression: VkExpression): SvExpression {
            return when (expression) {
                is VkLambdaExpression -> {
                    throw VkExtractException("unable to extract lambda expression", expression.linePos)
                }
                is VkCallableExpression -> {
                    extractCallableExpressionAsExpression(expression)
                }
                is VkOperatorExpression -> {
                    extractOperatorExpression(expression)
                }
                is VkNavigationExpression -> {
                    throw VkExtractException("navigation suffixes are not supported", expression.linePos)
                }
                is VkIdentifierExpression -> {
                    SvIdentifierExpression(expression.linePos, expression.identifier)
                }
                is VkLiteralExpression -> {
                    SvLiteralExpression(expression.linePos, expression.value)
                }
                is VkStringExpression -> {
                    VkStringExtractor.extract(expression)
                }
            }
        }

        private fun extractCallableExpressionAsExpression(expression: VkCallableExpression): SvExpression {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw VkExtractException("only simple identifiers are supported in callable expressions", expression.linePos)
            }

            return when (identifier) {
                "wait" -> SvOperatorExpression(expression.linePos,
                        SvOperatorType.DELAY, listOf(expression.args[0].extractExpression()))
                "print" -> SvCallableExpression(expression.linePos,
                        SvLiteralExpression(expression.linePos, "\$write"), listOf(expression.args[0].extractExpression()))
                "println" -> SvCallableExpression(expression.linePos,
                        SvLiteralExpression(expression.linePos, "\$display"), listOf(expression.args[0].extractExpression()))
                "finish" -> SvCallableExpression(expression.linePos,
                        SvLiteralExpression(expression.linePos, "\$finish"), listOf())
                else -> throw VkExtractException("callable $identifier not supported", expression.linePos)
            }
        }

        private fun extractOperatorExpression(expression: VkOperatorExpression): SvExpression {
            val args = expression.args.map { extractExpression(it) }
            val linePos = expression.linePos
            return when (expression.type) {
                VkOperatorType.PUT -> SvOperatorExpression(linePos, SvOperatorType.BASSIGN, listOf(args[0], args[1]))
                VkOperatorType.REG -> SvOperatorExpression(linePos, SvOperatorType.NBASSIGN, listOf(args[0], args[1]))
                VkOperatorType.NOT -> SvOperatorExpression(linePos, SvOperatorType.NOT, listOf(args[0]))
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