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

package io.verik.core.vkx

import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import io.verik.core.kt.parseDeclaration
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.main.LineException
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
internal class ComponentInstanceTest {

    @Test
    fun `instantiation no annotation`() {
        val rule = AlRuleParser.parseDeclaration("val m = _m()")
        val declaration = parseDeclaration(rule)
        assertFalse(ComponentInstance.isComponentInstance(declaration))
        assertThrowsMessage<LineException>("component annotation expected") {
            ComponentInstance(declaration)
        }
    }

    @Test
    fun `instantiation simple`() {
        val rule = AlRuleParser.parseDeclaration("@comp val m = _m()")
        val declaration = parseDeclaration(rule)
        val componentInstance = ComponentInstance(declaration)
        val expected = ComponentInstance(
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
        val componentInstance = ComponentInstance(declaration)
        val expected = ComponentInstance(
                1,
                "m",
                Symbol(1, 1, 1),
                null,
                null,
                listOf(VkxConnection(
                        2,
                        null,
                        VkxExpressionProperty(2, TYPE_BOOL, null, null, null)
                ))
        )
        assertEquals(expected, componentInstance)
    }
}