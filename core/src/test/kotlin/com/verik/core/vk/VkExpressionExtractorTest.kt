package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkExpressionExtractorTest {

    @Test
    fun `bocking assignment`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.PUT, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkIdentifierExpression(LinePos.ZERO, "y")
        ))
        val expected = SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        ))
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `arithmetic add`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.ADD, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkIdentifierExpression(LinePos.ZERO, "y")
        ))
        val expected = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        ))
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `literal zero`() {
        val expression = VkLiteralExpression(LinePos.ZERO, "0")
        val expected = SvLiteralExpression(LinePos.ZERO, "0")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `forever loop`() {
        val expression = VkCallableExpression(LinePos.ZERO, VkIdentifierExpression(LinePos.ZERO, "forever"),
                listOf(VkLambdaExpression(LinePos.ZERO, listOf())))
        val expected = SvLoopStatement(LinePos.ZERO, "forever", listOf())
        assertEquals(expected, expression.extractStatement())
    }
}