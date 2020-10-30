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

package verik.core.vk.ast

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.vk.VkUtil

internal class VkModuleTest {

    @Test
    fun `illegal type`() {
        val string = "class _c: _class"
        val declaration = KtUtil.resolveDeclaration(string)
        assertFalse(VkModule.isModule(declaration))
        assertThrowsMessage<LineException>("expected type to inherit from module") {
            VkModule(declaration)
        }
    }

    @Test
    fun `module simple`() {
        val string = "class _m: _module"
        val expected = VkModule(
                1,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                false,
                listOf(),
                listOf(),
                listOf()
        )
        assertEquals(expected, VkUtil.parseModule(string))
    }

    @Test
    fun `module with port`() {
        val string = """
            class _m: _module {
                @input val x = _bool()
            }
        """.trimIndent()
        val expected = VkModule(
                1,
                "_m",
                Symbol(1, 1, 1),
                listOf(VkPort(
                        2,
                        "x",
                        Symbol(1, 1, 2),
                        TYPE_BOOL,
                        VkPortType.INPUT,
                        VkExpressionFunction(
                                2,
                                TYPE_BOOL,
                                FUNCTION_TYPE_BOOL,
                                null,
                                listOf()
                        )
                )),
                false,
                listOf(),
                listOf(),
                listOf()
        )
        assertEquals(expected, VkUtil.parseModule(string))
    }
}