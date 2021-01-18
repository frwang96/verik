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

internal class TxBuilderActionBlockTest {

    @Test
    fun `run action block empty`() {
        val string = """
            @run fun f() {}
        """.trimIndent()
        val expected = """
            initial begin: f
            end: f
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentActionBlock("", "", string)
        )
    }

    @Test
    fun `seq action block`() {
        val componentContext = """
            val clk = _bool()
        """.trimIndent()
        val string = """
            @seq fun f() {
                on (negedge(clk)) {}
            }
        """.trimIndent()
        val expected = """
            always_ff @(negedge clk) begin: f
            end: f
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentActionBlock("", componentContext, string)
        )
    }
}
