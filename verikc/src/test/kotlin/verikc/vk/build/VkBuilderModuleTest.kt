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

package verikc.vk.build

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkExpressionFunction
import verikc.vk.ast.VkModule
import verikc.vk.ast.VkPort

internal class VkBuilderModuleTest {

    @Test
    fun `module simple`() {
        val string = """
            class _m: _module
        """.trimIndent()
        val expected = VkModule(
            line(3),
            "_m",
            Symbol(3),
            listOf(),
            false,
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, VkBuildUtil.buildModule("", string))
    }

    @Test
    fun `module with port`() {
        val string = """
            class _m: _module {
                @input val x = _bool()
            }
        """.trimIndent()
        val expected = VkModule(
            line(3),
            "_m",
            Symbol(3),
            listOf(
                VkPort(
                    line(4),
                    "x",
                    Symbol(6),
                    TYPE_BOOL,
                    PortType.INPUT,
                    VkExpressionFunction(
                        line(4),
                        TYPE_BOOL,
                        FUNCTION_TYPE_BOOL,
                        null,
                        listOf()
                    )
                )
            ),
            false,
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, VkBuildUtil.buildModule("", string))
    }
}
