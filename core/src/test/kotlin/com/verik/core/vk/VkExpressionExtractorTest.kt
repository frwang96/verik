package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.SvFunctionExpression
import com.verik.core.sv.SvFunctionType
import com.verik.core.sv.SvIdentifierExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkExpressionExtractorTest {

    @Nested
    inner class Assignment {

        @Test
        fun `bocking assignment`() {
            val expression = VkFunctionExpression(LinePos.ZERO, "put", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos.ZERO, "x"),
                    VkIdentifierExpression(LinePos.ZERO, "y")
            ))
            val expected = SvFunctionExpression(LinePos.ZERO, "bassign", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvIdentifierExpression(LinePos.ZERO, "y")
            ))
            assertEquals(expected, expression.extract())
        }
    }

    @Nested
    inner class Arithmetic {

        @Test
        fun `add expression`() {
            val expression = VkFunctionExpression(LinePos.ZERO, "add", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos.ZERO, "x"),
                    VkIdentifierExpression(LinePos.ZERO, "y")
            ))
            val expected = SvFunctionExpression(LinePos.ZERO, "add", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvIdentifierExpression(LinePos.ZERO, "y")
            ))
            assertEquals(expected, expression.extract())
        }
    }
}