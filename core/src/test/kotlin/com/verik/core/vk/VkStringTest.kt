/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvCallableExpression
import com.verik.core.sv.SvIdentifierExpression
import com.verik.core.sv.SvStringExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class VkStringTest {

    @Test
    fun `parse string`() {
        val rule = KtRuleParser.parseExpression("\"x\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(LinePos(1, 1), listOf(
                VkStringSegmentLiteral("x")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse string reference`() {
        val rule = KtRuleParser.parseExpression("\"\$x\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(LinePos(1, 1), listOf(
                VkStringSegmentExpression(VkIdentifierExpression(LinePos(1, 3), "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse string expression`() {
        val rule = KtRuleParser.parseExpression("\"\${x}\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(LinePos(1, 1), listOf(
                VkStringSegmentExpression(VkIdentifierExpression(LinePos(1, 4), "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence`() {
        val rule = KtRuleParser.parseExpression("\"\\n\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(LinePos(1, 1), listOf(
                VkStringSegmentLiteral("\\n")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence converted`() {
        val rule = KtRuleParser.parseExpression("\"\\'\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(LinePos(1, 1), listOf(
                VkStringSegmentLiteral("'")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence illegal`() {
        val rule = KtRuleParser.parseExpression("\"\\u0000\"")
        val exception = assertThrows<LinePosException> {
            VkExpression(rule)
        }
        assertEquals("(1, 2) illegal escape sequence \\u0000", exception.message)
    }

    @Test
    fun `extract string`() {
        val expression = VkStringExpression(LinePos.ZERO, listOf(
                VkStringSegmentLiteral("x")
        ))
        val expected = SvStringExpression(LinePos.ZERO, "x")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `extract string segmented`() {
        val expression = VkStringExpression(LinePos.ZERO, listOf(
                VkStringSegmentLiteral("x"),
                VkStringSegmentLiteral("x")
        ))
        val expected = SvStringExpression(LinePos.ZERO, "xx")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `extract string expression`() {
        val expression = VkStringExpression(LinePos.ZERO, listOf(
                VkStringSegmentExpression(VkIdentifierExpression(LinePos.ZERO, "x"))
        ))
        val expected = SvCallableExpression(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "\$sformatf"), listOf(
                SvStringExpression(LinePos.ZERO, "0x%x"),
                SvIdentifierExpression(LinePos.ZERO, "x")
        ))
        assertEquals(expected, expression.extractExpression())
    }
}
