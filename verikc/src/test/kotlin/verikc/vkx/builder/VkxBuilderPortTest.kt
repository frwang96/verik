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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.line
import verikc.vkx.VkxBuildUtil
import verikc.vkx.ast.VkxPort
import verikc.vkx.ast.VkxProperty

internal class VkxBuilderPortTest {

    @Test
    fun `bool input`() {
        val string = "@input val x = _bool()"
        val expected = VkxPort(
            VkxProperty(line(4), "x", Symbol(6), TYPE_BOOL.toTypeGenerifiedInstance()),
            PortType.INPUT
        )
        Assertions.assertEquals(expected, VkxBuildUtil.buildModulePort("", string))
    }

    @Test
    fun `bool illegal type`() {
        val string = "@input @output val x = _bool()"
        assertThrowsMessage<LineException>("illegal port type") {
            VkxBuildUtil.buildModulePort("", string)
        }
    }

    @Test
    fun `ubit output`() {
        val string = "@output val x = _ubit(8)"
        val expected = VkxPort(
            VkxProperty(
                line(4),
                "x",
                Symbol(6),
                TYPE_UBIT.toTypeGenerifiedInstance(8)
            ),
            PortType.OUTPUT
        )
        Assertions.assertEquals(expected, VkxBuildUtil.buildModulePort("", string))
    }
}
