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
            class N : Module()
        """.trimIndent()
        val string = """
            @make val n = t_N()
        """.trimIndent()
        val expected = """
            N n ();
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }

    @Test
    fun `module with connection`() {
        val fileContext = """
            class N : Module() {
                @input var x = t_Boolean()
            }
        """.trimIndent()
        val moduleContext = """
            var y = t_Boolean()
        """.trimIndent()
        val string = """
            @make val n = t_N().with(y)
        """.trimIndent()
        val expected = """
            N n (
                .x (y)
            );
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        )
    }

    @Test
    fun `module with connection null`() {
        val fileContext = """
            class N : Module() {
                @output var x = t_Boolean()
            }
        """.trimIndent()
        val string = """
            @make val n = t_N().with(null)
        """.trimIndent()
        val expected = """
            N n (
                .x ()
            );
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }

    @Test
    fun `bus port simple`() {
        val fileContext = """
            class BP: BusPort()
        """.trimIndent()
        val string = """
            @make val bp = t_BP()
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
            class BP: BusPort() {
                @input var x = t_Boolean()
            }
        """.trimIndent()
        val busContext = """
            var x = t_Boolean()
        """.trimIndent()
        val string = """
            @make val bp = t_BP().with(x)
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

    @Test
    fun `clock port simple`() {
        val fileContext = """
            class CP: ClockPort()
        """.trimIndent()
        val string = """
            @make val cp = t_CP().with(posedge(false))
        """.trimIndent()
        val expected = """
            clocking cp @(posedge 1'b0);
            endclocking
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildBusComponentInstance(fileContext, "", string)
        )
    }
}
