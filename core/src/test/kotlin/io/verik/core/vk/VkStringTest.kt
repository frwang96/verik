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

package io.verik.core.vk

import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import io.verik.core.main.LineException
import io.verik.core.sv.SvExpressionCallable
import io.verik.core.sv.SvExpressionIdentifier
import io.verik.core.sv.SvExpressionString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkStringTest {

    @Test
    fun `parse string`() {
        val rule = AlRuleParser.parseExpression("\"x\"")
        val expression = VkExpression(rule)
        val expected = VkExpressionString(1, listOf(
                VkStringSegmentLiteral("x")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse string reference`() {
        val rule = AlRuleParser.parseExpression("\"\$x\"")
        val expression = VkExpression(rule)
        val expected = VkExpressionString(1, listOf(
                VkStringSegmentExpression(VkExpressionIdentifier(1, "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse string expression`() {
        val rule = AlRuleParser.parseExpression("\"\${x}\"")
        val expression = VkExpression(rule)
        val expected = VkExpressionString(1, listOf(
                VkStringSegmentExpression(VkExpressionIdentifier(1, "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence`() {
        val rule = AlRuleParser.parseExpression("\"\\n\"")
        val expression = VkExpression(rule)
        val expected = VkExpressionString(1, listOf(
                VkStringSegmentLiteral("\\n")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence converted`() {
        val rule = AlRuleParser.parseExpression("\"\\'\"")
        val expression = VkExpression(rule)
        val expected = VkExpressionString(1, listOf(
                VkStringSegmentLiteral("'")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence illegal`() {
        val rule = AlRuleParser.parseExpression("\"\\b\"")
        assertThrowsMessage<LineException>("illegal escape sequence \\b") {
            VkExpression(rule)
        }
    }

    @Test
    fun `extract string`() {
        val expression = VkExpressionString(0, listOf(
                VkStringSegmentLiteral("x")
        ))
        val expected = SvExpressionString(0, "x")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `extract string segmented`() {
        val expression = VkExpressionString(0, listOf(
                VkStringSegmentLiteral("x"),
                VkStringSegmentLiteral("x")
        ))
        val expected = SvExpressionString(0, "xx")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `extract string expression`() {
        val expression = VkExpressionString(0, listOf(
                VkStringSegmentExpression(VkExpressionIdentifier(0, "x"))
        ))
        val expected = SvExpressionCallable(0, SvExpressionIdentifier(0, "\$sformatf"), listOf(
                SvExpressionString(0, "0x%x"),
                SvExpressionIdentifier(0, "x")
        ))
        assertEquals(expected, expression.extractExpression())
    }
}
