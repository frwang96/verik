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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.kt.KtUtil
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

internal class VkActionBlockTest {

    @Test
    fun `action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("@task fun f() {}")
        val declaration = KtUtil.resolveDeclaration(rule)
        assertFalse(VkActionBlock.isActionBlock(declaration))
        assertThrowsMessage<LineException>("illegal action block type") {
            VkActionBlock(declaration)
        }
    }

    @Test
    fun `put action block`() {
        val rule = AlRuleParser.parseDeclaration("@put fun f() {}")
        val actionBlock = VkUtil.parseActionBlock(rule)
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

    @Disabled @Test
    fun `reg action block`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {
                on (posedge(clk)) {}
            }
        """.trimIndent())
        val actionBlock = VkUtil.parseActionBlock(rule)
        val expected = VkActionBlock(
                1,
                "f",
                Symbol(1, 1, 1),
                VkActionBlockType.REG,
                listOf(),
                VkBlock(2, listOf())
        )
        assertEquals(expected, actionBlock)
    }

    @Test
    fun `reg action block no on expression`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {}
        """.trimIndent())
        assertThrowsMessage<LineException>("on expression expected for reg block") {
            VkUtil.parseActionBlock(rule)
        }
    }

    @Disabled @Test
    fun `reg action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {
                on (posedge(clk)) {}
                0
            }
        """.trimIndent())
        assertThrowsMessage<LineException>("illegal use of on expression") {
            VkUtil.parseActionBlock(rule)
        }
    }

    @Disabled @Test
    fun `initial action block illegal`() {
        val rule = AlRuleParser.parseDeclaration("""
            @initial fun f() {
                on (posedge(clk)) {}
            }
        """.trimIndent())
        assertThrowsMessage<LineException>("on expression not permitted here") {
            VkUtil.parseActionBlock(rule)
        }
    }
}