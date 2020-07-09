package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvCallableExpression
import com.verik.core.sv.SvIdentifierExpression
import com.verik.core.sv.SvStringExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkStringTest {

    @Nested
    inner class Parse {

        @Test
        fun `simple string`() {
            val rule = KtRuleParser.parseExpression("\"x\"")
            val expression = VkExpression(rule)
            val expected = VkStringExpression(LinePos(1, 1), listOf(
                    VkStringSegmentLiteral("x")
            ))
            assertEquals(expected, expression)
        }

        @Test
        fun `simple string reference`() {
            val rule = KtRuleParser.parseExpression("\"\$x\"")
            val expression = VkExpression(rule)
            val expected = VkStringExpression(LinePos(1, 1), listOf(
                    VkStringSegmentExpression(VkIdentifierExpression(LinePos(1, 3), "x"))
            ))
            assertEquals(expected, expression)
        }

        @Test
        fun `simple string expression`() {
            val rule = KtRuleParser.parseExpression("\"\${x}\"")
            val expression = VkExpression(rule)
            val expected = VkStringExpression(LinePos(1, 1), listOf(
                    VkStringSegmentExpression(VkIdentifierExpression(LinePos(1, 4), "x"))
            ))
            assertEquals(expected, expression)
        }

        @Test
        fun `simple escape sequence`() {
            val rule = KtRuleParser.parseExpression("\"\\n\"")
            val expression = VkExpression(rule)
            val expected = VkStringExpression(LinePos(1, 1), listOf(
                    VkStringSegmentLiteral("\\n")
            ))
            assertEquals(expected, expression)
        }

        @Test
        fun `converted escape sequence`() {
            val rule = KtRuleParser.parseExpression("\"\\'\"")
            val expression = VkExpression(rule)
            val expected = VkStringExpression(LinePos(1, 1), listOf(
                    VkStringSegmentLiteral("'")
            ))
            assertEquals(expected, expression)
        }

        @Test
        fun `illegal escape sequence`() {
            val rule = KtRuleParser.parseExpression("\"\\u0000\"")
            assertThrows<VkParseException> {
                VkExpression(rule)
            }
        }
    }

    @Nested
    inner class Extract {

        @Test
        fun `simple string`() {
            val expression = VkStringExpression(LinePos.ZERO, listOf(
                    VkStringSegmentLiteral("x")
            ))
            val expected = SvStringExpression(LinePos.ZERO, "x")
            assertEquals(expected, expression.extract())
        }

        @Test
        fun `segmented string`() {
            val expression = VkStringExpression(LinePos.ZERO, listOf(
                    VkStringSegmentLiteral("x"),
                    VkStringSegmentLiteral("x")
            ))
            val expected = SvStringExpression(LinePos.ZERO, "xx")
            assertEquals(expected, expression.extract())
        }

        @Test
        fun `string with identifier`() {
            val expression = VkStringExpression(LinePos.ZERO, listOf(
                    VkStringSegmentExpression(VkIdentifierExpression(LinePos.ZERO, "x"))
            ))
            val expected = SvCallableExpression(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "\$sformatf"), listOf(
                    SvStringExpression(LinePos.ZERO, "%X"),
                    SvIdentifierExpression(LinePos.ZERO, "x")
            ))
            assertEquals(expected, expression.extract())
        }
    }
}
