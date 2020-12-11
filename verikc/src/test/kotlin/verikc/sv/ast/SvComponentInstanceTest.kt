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
import verikc.base.ast.Line
import verikc.sv.build.SvSourceBuilder

internal class SvComponentInstanceTest {

    @Test
    fun `component instance simple`() {
        val componentInstance = SvComponentInstance(
                Line(0),
                "m0",
                "m",
                listOf()
        )
        val expected = "m m0 ();"
        val builder = SvSourceBuilder()
        componentInstance.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `component instance with connection`() {
        val componentInstance = SvComponentInstance(
                Line(0),
                "m0",
                "m",
                listOf(SvConnection(Line(0), "x", "y"))
        )
        val expected = """
            m m0 (
                .x (y)
            );
        """.trimIndent()
        val builder = SvSourceBuilder()
        componentInstance.build(builder)
        assertStringEquals(expected, builder)
    }
}
