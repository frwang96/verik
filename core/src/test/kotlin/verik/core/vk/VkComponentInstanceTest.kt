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
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.main.LineException
import verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
internal class VkComponentInstanceTest {

    @Test
    fun `instantiation no annotation`() {
        val rule = AlRuleParser.parseDeclaration("val m = _m()")
        val declaration = parseDeclaration(rule)
        assertFalse(VkComponentInstance.isComponentInstance(declaration))
        assertThrowsMessage<LineException>("component annotation expected") {
            VkComponentInstance(declaration)
        }
    }

    @Test
    fun `instantiation simple`() {
        val rule = AlRuleParser.parseDeclaration("@comp val m = _m()")
        val declaration = parseDeclaration(rule)
        val componentInstance = VkComponentInstance(declaration)
        val expected = VkComponentInstance(
                1,
                "m",
                Symbol(1, 1, 1),
                null,
                null,
                listOf()
        )
        assertEquals(expected, componentInstance)
    }

    @Test
    fun `instantiation with connection`() {
        val rule = AlRuleParser.parseDeclaration("""
            @comp val m = _m() with {
                it.clk con clk
            }
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        val componentInstance = VkComponentInstance(declaration)
        val expected = VkComponentInstance(
                1,
                "m",
                Symbol(1, 1, 1),
                null,
                null,
                listOf(VkConnection(
                        2,
                        null,
                        VkExpressionProperty(2, TYPE_BOOL, null, null)
                ))
        )
        assertEquals(expected, componentInstance)
    }
}