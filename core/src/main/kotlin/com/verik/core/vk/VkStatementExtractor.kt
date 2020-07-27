package com.verik.core.vk

import com.verik.core.LinePosException
import com.verik.core.sv.SvConditionalStatement
import com.verik.core.sv.SvExpressionStatement
import com.verik.core.sv.SvLoopStatement
import com.verik.core.sv.SvStatement

// Copyright (c) 2020 Francis Wang

class VkStatementExtractor {

    companion object {

        fun extractStatement(expression: VkExpression): SvStatement {
            val statement = when (expression) {
                is VkCallableExpression -> {
                    extractCallableExpression(expression)
                }
                is VkOperatorExpression -> {
                    extractOperatorExpression(expression)
                }
                else -> null
            }
            return statement ?: SvExpressionStatement(expression.linePos, VkExpressionExtractor.extractExpression(expression))
        }

        private fun extractCallableExpression(expression: VkCallableExpression): SvStatement? {
            val identifier = expression.target.let {
                if (it is VkIdentifierExpression) it.identifier
                else throw LinePosException("only simple identifiers are supported in callable expressions", expression.linePos)
            }

            return when (identifier) {
                "forever" -> SvLoopStatement(expression.linePos, "forever", extractLambdaExpression(expression.args[0]))
                else -> null
            }
        }

        private fun extractOperatorExpression(expression: VkOperatorExpression): SvStatement? {
            return when (expression.type) {
                VkOperatorType.IF -> {
                    val ifExpression = expression.args[0].extractExpression()
                    val ifBody = extractLambdaExpression(expression.args[1])
                    SvConditionalStatement(expression.linePos, ifExpression, ifBody, listOf())
                }
                VkOperatorType.IF_ELSE -> {
                    val ifExpression = expression.args[0].extractExpression()
                    val ifBody = extractLambdaExpression(expression.args[1])
                    val elseBody = extractLambdaExpression(expression.args[2])
                    SvConditionalStatement(expression.linePos, ifExpression, ifBody, elseBody)
                }
                else -> null
            }
        }

        private fun extractLambdaExpression(expression: VkExpression): List<SvStatement> {
            return if (expression is VkLambdaExpression) {
                expression.statements.map { it.extract() }
            } else throw LinePosException("lambda expression expected", expression.linePos)
        }
    }
}