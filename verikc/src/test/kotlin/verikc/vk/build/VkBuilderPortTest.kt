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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkConnectionType
import verikc.vk.ast.VkPort
import verikc.vk.ast.VkProperty

internal class VkBuilderPortTest {

    @Test
    fun `illegal type`() {
        val string = "@input @output val x = t_Boolean()"
        assertThrowsMessage<LineException>("illegal connection type") {
            VkBuildUtil.buildModulePort("", string)
        }
    }

    @Test
    fun `input boolean`() {
        val string = "@input val x = t_Boolean()"
        val expected = VkPort(
            VkProperty(line(4), "x", Symbol(7), MutabilityType.VAL, TYPE_BOOLEAN.toTypeGenerified()),
            VkConnectionType.INPUT,
            PortType.INPUT
        )
        Assertions.assertEquals(expected, VkBuildUtil.buildModulePort("", string))
    }

    @Test
    fun `output ubit`() {
        val string = "@output val x = t_Ubit(8)"
        val expected = VkPort(
            VkProperty(line(4), "x", Symbol(7), MutabilityType.VAL, TYPE_UBIT.toTypeGenerified(8)),
            VkConnectionType.OUTPUT,
            PortType.OUTPUT
        )
        Assertions.assertEquals(expected, VkBuildUtil.buildModulePort("", string))
    }

    @Test
    fun `inout bus`() {
        val fileContext = """
            class B: Bus()
        """.trimIndent()
        val string = "@inout val b = t_B()"
        val expected = VkPort(
            VkProperty(line(4), "b", Symbol(10), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            VkConnectionType.INOUT,
            PortType.BUS
        )
        Assertions.assertEquals(expected, VkBuildUtil.buildModulePort(fileContext, string))
    }
}
