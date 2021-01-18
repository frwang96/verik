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

internal class TxBuilderMethodBlockTest {

    @Test
    fun `function no parameters`() {
        val string = """
            fun f() {}
        """.trimIndent()
        val expected = """
            function automatic void f ();
            endfunction
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentMethodBlock("", "", string)
        )
    }

    @Test
    fun `function one parameter`() {
        val string = """
            fun f(x: _int) {}
        """.trimIndent()
        val expected = """
            function automatic void f (
                int x
            );
            endfunction
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentMethodBlock("", "", string)
        )
    }

    @Test
    fun `function with statement`() {
        val string = """
            fun f() {
                0
            }
        """.trimIndent()
        val expected = """
            function automatic void f ();
                0;
            endfunction
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentMethodBlock("", "", string)
        )
    }

    @Test
    fun `function with local property`() {
        val string = """
            fun f() {
                val x = 0
            }
        """.trimIndent()
        val expected = """
            function automatic void f ();
                int x;
                x = 0;
            endfunction
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentMethodBlock("", "", string)
        )
    }

    @Test
    fun `task no parameters`() {
        val string = """
            @task fun f() {}
        """.trimIndent()
        val expected = """
            task automatic f ();
            endtask
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentMethodBlock("", "", string)
        )
    }

    @Test
    fun `task one parameter`() {
        val string = """
            @task fun f(x: _int) {}
        """.trimIndent()
        val expected = """
            task automatic f (
                int x
            );
            endtask
        """.trimIndent()
        assertStringEquals(
            expected,
            TxBuildUtil.buildComponentMethodBlock("", "", string)
        )
    }
}
