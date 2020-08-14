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

package verik.core.vk

import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.kt.parseDeclaration
import verik.core.main.LineException
import verik.core.sv.SvActionBlock
import verik.core.sv.SvActionBlockType
import verik.core.sv.SvBlock
import verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class VkActionBlockTest {

    @Test
    fun `action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("@task fun f() {}")
        val declaration = parseDeclaration(rule)
        assertFalse(VkActionBlock.isActionBlock(declaration))
        assertThrowsMessage<LineException>("illegal action block type") {
            VkActionBlock(declaration)
        }
    }

    @Test
    fun `put action block`() {
        val rule = AlRuleParser.parseDeclaration("@put fun f() {}")
        val declaration = parseDeclaration(rule)
        val actionBlock = VkActionBlock(declaration)
        val expected = VkActionBlock(
                1,
                "f",
                Symbol(1, 1, 1),
                VkActionBlockType.PUT,
                null,
                VkBlock(1, listOf())
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
        val actionBlock = VkActionBlock(declaration)
        val expected = VkActionBlock(
                1,
                "f",
                Symbol(1, 1, 1),
                VkActionBlockType.REG,
                listOf(VkEdge(2, VkEdgeType.NEGEDGE, null)),
                VkBlock(2, listOf())
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
            VkActionBlock(declaration)
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
            VkActionBlock(declaration)
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
            VkActionBlock(declaration)
        }
    }

    @Test
    fun `extract initial action block empty`() {
        val rule = AlRuleParser.parseDeclaration("""
            @initial fun f() {}
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        val actionBlock = VkActionBlock(declaration).extract()
        val expected = SvActionBlock(
                1,
                SvActionBlockType.INITIAL,
                SvBlock(1, listOf())
        )
        assertEquals(expected, actionBlock)
    }
}