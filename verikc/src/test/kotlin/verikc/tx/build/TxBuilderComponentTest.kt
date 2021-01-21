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

internal class TxBuilderComponentTest {

    @Test
    fun `module empty`() {
        val string = """
            class M : Module()
        """.trimIndent()
        val expected = """
            module M;

            endmodule: M
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildComponent("", string))
    }

    @Test
    fun `module with port`() {
        val string = """
            class M : Module() {
                @output var x = _ubit(8)
            }
        """.trimIndent()
        val expected = """
            module M (
                output logic [7:0] x
            );

            endmodule: M
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildComponent("", string))
    }

    @Test
    fun `module with property`() {
        val string = """
            class M : Module() {
                var x = _bool()
            }
        """.trimIndent()
        val expected = """
            module M;

                logic x;

            endmodule: M
        """.trimIndent()
        assertStringEquals(expected, TxBuildUtil.buildComponent("", string))
    }
}
