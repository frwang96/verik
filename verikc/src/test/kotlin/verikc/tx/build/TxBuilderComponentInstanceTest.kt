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

package verikc.tx.build

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.tx.TxBuildUtil

internal class TxBuilderComponentInstanceTest {

    @Test
    fun `module simple`() {
        val fileContext = """
            class _n: _module()
        """.trimIndent()
        val string = """
            @make val n0 = _n()
        """.trimIndent()
        val expected = """
            n n0 ();
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }

    @Test
    fun `module with connection`() {
        val fileContext = """
            class _n: _module() {
                @input var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var y = _bool()
        """.trimIndent()
        val string = """
            @make val n0 = _n() with {
                it.x = y
            }
        """.trimIndent()
        val expected = """
            n n0 (
                .x (y)
            );
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        )
    }

    @Test
    fun `bus port simple`() {
        val fileContext = """
            class _bp: _busport()
        """.trimIndent()
        val string = """
            @make val bp = _bp()
        """.trimIndent()
        val expected = """
            modport bp ();
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildBusComponentInstance(fileContext, "", string)
        )
    }

    @Test
    fun `bus port with connection`() {
        val fileContext = """
            class _bp: _busport() {
                @input var x = _bool()
            }
        """.trimIndent()
        val busContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val bp = _bp() with {
                it.x = x
            }
        """.trimIndent()
        val expected = """
            modport bp (
                input x
            );
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildBusComponentInstance(fileContext, busContext, string)
        )
    }
}
