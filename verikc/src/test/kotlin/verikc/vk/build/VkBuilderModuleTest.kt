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

package verikc.vk.build

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.MutabilityType
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkModule
import verikc.vk.ast.VkPort
import verikc.vk.ast.VkProperty

internal class VkBuilderModuleTest {

    @Test
    fun `module simple`() {
        val string = """
            class _m: _module()
        """.trimIndent()
        val expected = VkModule(
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
        assertEquals(
            expected,
            VkBuildUtil.buildModule("", string)
        )
    }

    @Test
    fun `module with port`() {
        val string = """
            class _m: _module() {
                @input val x = _bool()
            }
        """.trimIndent()
        val expected = VkModule(
            line(3),
            "_m",
            Symbol(3),
            false,
            listOf(
                VkPort(
                    VkProperty(line(4), "x", Symbol(7), MutabilityType.VAL, TYPE_BOOL.toTypeGenerified()),
                    PortType.INPUT
                )
            ),
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModule("", string)
        )
    }

    @Test
    fun `module with property`() {
        val string = "val x = _bool()"
        val expected = VkProperty(line(4), "x", Symbol(7), MutabilityType.VAL, TYPE_BOOL.toTypeGenerified())
        assertEquals(
            expected,
            VkBuildUtil.buildModuleProperty("", string)
        )
    }
}
