package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkStatementExtractorTest {

    @Test
    fun `forever loop`() {
        val expression = VkCallableExpression(LinePos.ZERO, VkIdentifierExpression(LinePos.ZERO, "forever"),
                listOf(VkLambdaExpression(LinePos.ZERO, listOf())))
        val expected = SvLoopStatement(LinePos.ZERO, "forever", listOf())
        assertEquals(expected, expression.extractStatement())
    }

    @Test
    fun `if statement`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.IF, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkLambdaExpression(LinePos.ZERO,
                        listOf(VkStatement(VkLiteralExpression(LinePos.ZERO, "0"), LinePos.ZERO)))
        ))
        val expected = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))), listOf())
        assertEquals(expected, expression.extractStatement())
    }

    @Test
    fun `if else statement`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.IF_ELSE, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkLambdaExpression(LinePos.ZERO,
                        listOf(VkStatement(VkLiteralExpression(LinePos.ZERO, "0"), LinePos.ZERO))),
                VkLambdaExpression(LinePos.ZERO,
                        listOf(VkStatement(VkLiteralExpression(LinePos.ZERO, "1"), LinePos.ZERO)))
        ))
        val expected = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "1"))))
        assertEquals(expected, expression.extractStatement())
    }
}