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

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.assert.assertThrowsMessage
import io.verik.core.al.AlRuleParser
import io.verik.core.sv.SvCallableExpression
import io.verik.core.sv.SvIdentifierExpression
import io.verik.core.sv.SvStringExpression
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkStringTest {

    @Test
    fun `parse string`() {
        val rule = AlRuleParser.parseExpression("\"x\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(FileLine(1), listOf(
                VkStringSegmentLiteral("x")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse string reference`() {
        val rule = AlRuleParser.parseExpression("\"\$x\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(FileLine(1), listOf(
                VkStringSegmentExpression(VkIdentifierExpression(FileLine(1), "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse string expression`() {
        val rule = AlRuleParser.parseExpression("\"\${x}\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(FileLine(1), listOf(
                VkStringSegmentExpression(VkIdentifierExpression(FileLine(1), "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence`() {
        val rule = AlRuleParser.parseExpression("\"\\n\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(FileLine(1), listOf(
                VkStringSegmentLiteral("\\n")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence converted`() {
        val rule = AlRuleParser.parseExpression("\"\\'\"")
        val expression = VkExpression(rule)
        val expected = VkStringExpression(FileLine(1), listOf(
                VkStringSegmentLiteral("'")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `parse escape sequence illegal`() {
        val rule = AlRuleParser.parseExpression("\"\\u0000\"")
        assertThrowsMessage<FileLineException>("illegal escape sequence \\u0000") {
            VkExpression(rule)
        }
    }

    @Test
    fun `extract string`() {
        val expression = VkStringExpression(FileLine(), listOf(
                VkStringSegmentLiteral("x")
        ))
        val expected = SvStringExpression(FileLine(), "x")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `extract string segmented`() {
        val expression = VkStringExpression(FileLine(), listOf(
                VkStringSegmentLiteral("x"),
                VkStringSegmentLiteral("x")
        ))
        val expected = SvStringExpression(FileLine(), "xx")
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `extract string expression`() {
        val expression = VkStringExpression(FileLine(), listOf(
                VkStringSegmentExpression(VkIdentifierExpression(FileLine(), "x"))
        ))
        val expected = SvCallableExpression(FileLine(), SvIdentifierExpression(FileLine(), "\$sformatf"), listOf(
                SvStringExpression(FileLine(), "0x%x"),
                SvIdentifierExpression(FileLine(), "x")
        ))
        assertEquals(expected, expression.extractExpression())
    }
}
