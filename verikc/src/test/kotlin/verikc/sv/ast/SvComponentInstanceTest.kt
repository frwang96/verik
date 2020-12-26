/*
 * Copyright 2020 Francis Wang
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

package verikc.sv.ast

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.sv.SvBuildUtil

internal class SvComponentInstanceTest {

    @Test
    fun `component instance simple`() {
        val fileContext = """
            class _n: _module {}
        """.trimIndent()
        val string = """
            @make val n0 = _n()
        """.trimIndent()
        val expected = "n n0 ();"
        assertStringEquals(expected, SvBuildUtil.buildComponentInstance(fileContext, "", string))
    }

    @Test
    fun `component instance with connection`() {
        val fileContext = """
            class _n: _module {
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
        assertStringEquals(expected, SvBuildUtil.buildComponentInstance(fileContext, moduleContext, string))
    }
}
