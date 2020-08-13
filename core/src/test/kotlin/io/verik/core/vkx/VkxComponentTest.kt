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
import io.verik.core.kt.resolve.KtResolver
import io.verik.core.lang.LangSymbol.FUN_BOOL_TYPE
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.main.LineException
import io.verik.core.svx.SvxModule
import io.verik.core.symbol.Symbol
import io.verik.core.vkx.resolve.VkxResolver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class VkxComponentTest {

    @Test
    fun `module illegal type`() {
        val rule = AlRuleParser.parseDeclaration("class _c: _class")
        val declaration = parseDeclaration(rule)
        assertFalse(VkxComponent.isComponent(declaration))
        assertThrowsMessage<LineException>("illegal component type") {
            VkxComponent(declaration)
        }
    }

    @Test
    fun `module illegal name`() {
        val rule = AlRuleParser.parseDeclaration("class m: _module")
        val declaration = parseDeclaration(rule)
        assertThrowsMessage<LineException>("component identifier should begin with a single underscore") {
            VkxComponent(declaration)
        }
    }

    @Test
    fun `module simple`() {
        val rule = AlRuleParser.parseDeclaration("class _m: _module")
        val declaration = parseDeclaration(rule)
        val component = VkxComponent(declaration)
        val expected = VkxComponent(
                1,
                "_m",
                Symbol(1, 1, 1),
                VkxComponentType.MODULE,
                false,
                listOf(),
                listOf(),
                listOf(),
                listOf()
        )
        assertEquals(expected, component)
    }

    @Test
    fun `module with port`() {
        val rule = AlRuleParser.parseDeclaration("""
            class _m: _module {
                @input val x = _bool()
            }
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        KtResolver.resolveDeclaration(declaration)
        val component = VkxComponent(declaration)
        VkxResolver.resolveComponent(component)
        val expected = VkxComponent(
                1,
                "_m",
                Symbol(1, 1, 2),
                VkxComponentType.MODULE,
                false,
                listOf(VkxPort(
                        2,
                        "x",
                        Symbol(1, 1, 1),
                        VkxPortType.INPUT,
                        VkxExpressionFunction(
                                2,
                                TYPE_BOOL,
                                VkxType(TYPE_BOOL, listOf()),
                                null,
                                listOf(),
                                FUN_BOOL_TYPE
                        )
                )),
                listOf(),
                listOf(),
                listOf()
        )
        assertEquals(expected, component)
    }

    @Test
    fun `extract module`() {
        val rule = AlRuleParser.parseDeclaration("class _m: _module")
        val module = VkxComponent(parseDeclaration(rule)).extractModule()
        val expected = SvxModule(
                1,
                "m",
                listOf(),
                listOf()
        )
        assertEquals(expected, module)
    }
}