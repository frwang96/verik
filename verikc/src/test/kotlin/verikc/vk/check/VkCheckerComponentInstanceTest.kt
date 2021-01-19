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
import verikc.vk.VkBuildUtil

internal class VkCheckerComponentInstanceTest {

    @Test
    fun `connection valid`() {
        val fileContext = """
            class _n: _module() {
                @input var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
            }
        """.trimIndent()
        VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
    }

    @Test
    fun `connection duplicate`() {
        val fileContext = """
            class _n: _module() {
                @input var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
                it.x = x
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("duplicate connection [[7]]") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        }
    }

    @Test
    fun `connection invalid`() {
        val fileContext = """
            class _n: _module() {
                var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("invalid connection [[7]]") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        }
    }

    @Test
    fun `connection missing`() {
        val fileContext = """
            class _n: _module() {
                @input var x = _bool()
            }
        """.trimIndent()
        val string = """
            @make val n = _n() with {}
        """.trimIndent()
        assertThrowsMessage<LineException>("missing connection [[7]]") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        }
    }

    @Test
    fun `connection type mismatch`() {
        val fileContext = """
            class _n: _module() {
                @output var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("output assignment expected for [[7]]") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        }
    }

    @Test
    fun `bus port identifier mismatch`() {
        val fileContext = """
            class _bp: _busport() {
                @input var x = _bool()
            }
        """.trimIndent()
        val busContext = """
            var y = _bool()
        """.trimIndent()
        val string = """
            @make val bp = _bp() with {
                it.x = y
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("bus port connection identifiers must match") {
            VkBuildUtil.buildBusComponentInstance(fileContext, busContext, string)
        }
    }

    @Test
    fun `bus port in module invalid`() {
        val fileContext = """
            class _bp: _busport()
        """.trimIndent()
        val string = """
            @make val bp = _bp()
        """.trimIndent()
        assertThrowsMessage<LineException>("bus port not allowed in module") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        }
    }

    @Test
    fun `clock port invalid`() {
        val fileContext = """
            class _cp: _clockport()
        """.trimIndent()
        val string = """
            @make val cp = _cp()
        """.trimIndent()
        assertThrowsMessage<LineException>("on expression expected for clock port instantiation") {
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        }
    }
}