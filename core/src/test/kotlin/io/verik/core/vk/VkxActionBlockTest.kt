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

import io.verik.core.LineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assert.assertThrowsMessage
import io.verik.core.kt.parseDeclaration
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class VkxActionBlockTest {

    @Test
    fun `action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("@task fun f() {}")
        val declaration = parseDeclaration(rule)
        assertFalse(VkxActionBlock.isActionBlock(declaration))
        assertThrowsMessage<LineException>("illegal action block type") {
            VkxActionBlock(declaration)
        }
    }

    @Test
    fun `put action block`() {
        val rule = AlRuleParser.parseDeclaration("@put fun f() {}")
        val declaration = parseDeclaration(rule)
        val actionBlock = VkxActionBlock(declaration)
        val expected = VkxActionBlock(
                1,
                "f",
                Symbol(1, 1, 1),
                VkxActionBlockType.PUT,
                null,
                VkxBlock(1, listOf())
        )
        assertEquals(expected, actionBlock)
    }

    @Test
    fun `reg action block`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {
                on (posedge(clk)) {}
            }
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        val actionBlock = VkxActionBlock(declaration)
        val expected = VkxActionBlock(
                1,
                "f",
                Symbol(1, 1, 1),
                VkxActionBlockType.REG,
                listOf(VkxEdge(2, VkxEdgeType.NEGEDGE, null)),
                VkxBlock(2, listOf())
        )
        assertEquals(expected, actionBlock)
    }

    @Test
    fun `reg action block no edges`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {}
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        assertThrowsMessage<LineException>("edges expected for reg block") {
            VkxActionBlock(declaration)
        }
    }

    @Test
    fun `reg action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {
                on (posedge(clk)) {}
                0
            }
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        assertThrowsMessage<LineException>("illegal use of on expression") {
            VkxActionBlock(declaration)
        }
    }

    @Test
    fun `initial action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("""
            @initial fun f() {
                on (posedge(clk)) {}
            }
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        assertThrowsMessage<LineException>("edges not permitted here") {
            VkxActionBlock(declaration)
        }
    }
}