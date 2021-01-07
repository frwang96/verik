/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.vkx.builder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.line
import verikc.vkx.VkxBuildUtil
import verikc.vkx.ast.VkxModule
import verikc.vkx.ast.VkxPort
import verikc.vkx.ast.VkxProperty

internal class VkxBuilderModuleTest {

    @Test
    fun `module simple`() {
        val string = """
            class _m: _module()
        """.trimIndent()
        val expected = VkxModule(
            line(3),
            "_m",
            Symbol(3),
            false,
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, VkxBuildUtil.buildModule("", string))
    }

    @Test
    fun `module with port`() {
        val string = """
            class _m: _module() {
                @input val x = _bool()
            }
        """.trimIndent()
        val expected = VkxModule(
            line(3),
            "_m",
            Symbol(3),
            false,
            listOf(
                VkxPort(
                    VkxProperty(line(4), "x", Symbol(6), TYPE_BOOL.toTypeGenerifiedInstance()),
                    PortType.INPUT
                )
            ),
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, VkxBuildUtil.buildModule("", string))
    }

    @Test
    fun `module with property`() {
        val string = "val x = _bool()"
        val expected = VkxProperty(
            line(4),
            "x",
            Symbol(6),
            TYPE_BOOL.toTypeGenerifiedInstance()
        )
        assertEquals(expected, VkxBuildUtil.buildModuleProperty("", string))
    }
}
