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
            val expression = VkFunctionExpression(LinePos(0, 0), "put", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos(0, 0), "x"),
                    VkIdentifierExpression(LinePos(0, 0), "y")
            ))
            val expected = SvFunctionExpression("bassign", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression("x"),
                    SvIdentifierExpression("y")
            ))
            assertEquals(expected, expression.extract())
        }
    }

    @Nested
    inner class Arithmetic {

        @Test
        fun `add expression`() {
            val expression = VkFunctionExpression(LinePos(0, 0), "add", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos(0, 0), "x"),
                    VkIdentifierExpression(LinePos(0, 0), "y")
            ))
            val expected = SvFunctionExpression("add", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression("x"),
                    SvIdentifierExpression("y")
            ))
            assertEquals(expected, expression.extract())
        }
    }
}