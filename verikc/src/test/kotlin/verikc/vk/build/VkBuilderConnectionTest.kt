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
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkConnection
import verikc.vk.ast.VkConnectionType
import verikc.vk.ast.VkExpressionProperty

internal class VkBuilderConnectionTest {

    @Test
    fun `input connection`() {
        val fileContext = """
            class N : Module() {
                @input val x = t_Boolean()
            }
        """.trimIndent()
        val moduleContext = """
            val y = t_Boolean()
        """.trimIndent()
        val string = """
            @make val n = t_N() with {
                it.x = y
            }
        """.trimIndent()
        val expected = VkConnection(
            line(8),
            Symbol(7),
            VkConnectionType.INPUT,
            false,
            VkExpressionProperty(line(8), TYPE_BOOLEAN.toTypeGenerified(), Symbol(12), null),
            PortType.INPUT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, moduleContext, string)
        )
    }

    @Test
    fun `output connection`() {
        val fileContext = """
            class N : Module() {
                @output val x = t_Boolean()
            }
        """.trimIndent()
        val moduleContext = """
            val y = t_Boolean()
        """.trimIndent()
        val string = """
            @make val n = t_N() with {
                y = it.x
            }
        """.trimIndent()
        val expected = VkConnection(
            line(8),
            Symbol(7),
            VkConnectionType.OUTPUT,
            false,
            VkExpressionProperty(line(8), TYPE_BOOLEAN.toTypeGenerified(), Symbol(12), null),
            PortType.OUTPUT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, moduleContext, string)
        )
    }

    @Test
    fun `inout connection`() {
        val fileContext = """
            class N : Module() {
                @inout val x = t_Boolean()
            }
        """.trimIndent()
        val moduleContext = """
            val y = t_Boolean()
        """.trimIndent()
        val string = """
            @make val n = t_N() with {
                it.x con y
            }
        """.trimIndent()
        val expected = VkConnection(
            line(8),
            Symbol(7),
            VkConnectionType.INOUT,
            false,
            VkExpressionProperty(line(8), TYPE_BOOLEAN.toTypeGenerified(), Symbol(12), null),
            PortType.INOUT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, moduleContext, string)
        )
    }

    @Test
    fun `bus connection`() {
        val fileContext = """
            class B: Bus()
            class N : Module() {
                @inout val b = t_B()
            }
        """.trimIndent()
        val moduleContext = """
            @make val b = t_B()
        """.trimIndent()
        val string = """
            @make val n = t_N() with {
                it.b con b
            }
        """.trimIndent()
        val expected = VkConnection(
            line(9),
            Symbol(11),
            VkConnectionType.INOUT,
            true,
            VkExpressionProperty(line(9), Symbol(3).toTypeGenerified(), Symbol(16), null),
            PortType.BUS
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, moduleContext, string)
        )
    }
}
