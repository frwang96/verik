package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.SvIdentifierExpression
import com.verik.core.sv.SvOperatorExpression
import com.verik.core.sv.SvOperatorType
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
        assertEquals(expected, expression.extract())
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
        assertEquals(expected, expression.extract())
    }
}