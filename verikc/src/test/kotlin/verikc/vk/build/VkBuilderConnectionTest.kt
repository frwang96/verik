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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import verikc.base.ast.ConnectionType
import verikc.base.symbol.Symbol
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkConnection

@Disabled
// TODO requires with operator to generify lambda properties
internal class VkBuilderConnectionTest {

    @Test
    fun `input connection`() {
        val fileContext = """
            class _n: _module() {
                @input val x = _bool()
            }
        """.trimIndent()
        val string = """
            val y = _bool()
            @make val n = _n() with {
                it.x = y
            }
        """.trimIndent()
        val expected = VkConnection(
            line(8),
            Symbol(6),
            Symbol(10),
            ConnectionType.INPUT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, string)
        )
    }

    @Test
    fun `output connection`() {
        val fileContext = """
            class _n: _module() {
                @input val x = _bool()
            }
        """.trimIndent()
        val string = """
            val y = _bool()
            @make val n = _n() with {
                y = it.x
            }
        """.trimIndent()
        val expected = VkConnection(
            line(8),
            Symbol(6),
            Symbol(10),
            ConnectionType.OUTPUT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, string)
        )
    }

    @Test
    fun `inout connection`() {
        val fileContext = """
            class _n: _module() {
                @input val x = _bool()
            }
        """.trimIndent()
        val string = """
            val y = _bool()
            @make val n = _n() with {
                it.x con y
            }
        """.trimIndent()
        val expected = VkConnection(
            line(8),
            Symbol(6),
            Symbol(10),
            ConnectionType.INOUT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstanceConnection(fileContext, string)
        )
    }
}
