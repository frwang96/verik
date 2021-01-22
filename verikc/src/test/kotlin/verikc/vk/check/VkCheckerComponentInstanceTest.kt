/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.vk.check

import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.vk.VkBuildUtil

internal class VkCheckerComponentInstanceTest {

    @Test
    fun `connection valid`() {
        val fileContext = """
            class N : Module() {
                @input var x = t_Boolean()
            }
        """.trimIndent()
        val moduleContext = """
            var x = t_Boolean()
        """.trimIndent()
        val string = """
            @make val n = t_N().with(x)
        """.trimIndent()
        VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
    }

    @Test
    fun `connection type mismatch`() {
        val fileContext = """
            class N : Module() {
                @input var x = t_Ubit(8)
            }
        """.trimIndent()
        val moduleContext = """
            var x = t_Ubit(16)
        """.trimIndent()
        val string = """
            @make val n = t_N().with(x)
        """.trimIndent()
        assertThrowsMessage<LineException>("connection type mismatch expected $TYPE_UBIT(8) but got $TYPE_UBIT(16)") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        }
    }

    @Test
    fun `bus port identifier mismatch`() {
        val fileContext = """
            class BP: BusPort() {
                @input var x = t_Boolean()
            }
        """.trimIndent()
        val busContext = """
            var y = t_Boolean()
        """.trimIndent()
        val string = """
            @make val bp = t_BP().with(y)
        """.trimIndent()
        assertThrowsMessage<LineException>("connection identifiers must match") {
            VkBuildUtil.buildBusComponentInstance(fileContext, busContext, string)
        }
    }

    @Test
    fun `bus port in module invalid`() {
        val fileContext = """
            class BP: BusPort()
        """.trimIndent()
        val string = """
            @make val bp = t_BP()
        """.trimIndent()
        assertThrowsMessage<LineException>("bus port not allowed in module") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        }
    }

    @Test
    fun `clock port invalid`() {
        val fileContext = """
            class CP: ClockPort()
        """.trimIndent()
        val string = """
            @make val cp = t_CP()
        """.trimIndent()
        assertThrowsMessage<LineException>("event argument expected for clock port instantiation") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        }
    }
}