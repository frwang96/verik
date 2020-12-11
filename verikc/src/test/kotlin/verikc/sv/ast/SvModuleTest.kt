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
import verikc.base.ast.PortType
import verikc.sv.build.SvSourceBuilder

internal class SvModuleTest {

    @Test
    fun `module empty`() {
        val module = SvModule(
            Line(0),
            "m",
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
        val expected = """
            module m;
                timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SvSourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `module with port`() {
        val module = SvModule(
            Line(0),
            "m",
            listOf(
                SvPort(
                    Line(0),
                    PortType.OUTPUT,
                    SvTypeExtracted("logic", "[7:0]", ""),
                    "x"
                )
            ),
            listOf(),
            listOf(),
            listOf()
        )
        val expected = """
            module m (
                output logic [7:0] x
            );
                timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SvSourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `module with primary property`() {
        val module = SvModule(
            Line(0),
            "m",
            listOf(),
            listOf(
                SvPrimaryProperty(
                    Line(0),
                    SvTypeExtracted("logic", "", ""),
                    "x"
                )
            ),
            listOf(),
            listOf()
        )
        val expected = """
            module m;
                timeunit 1ns / 1ns;

                logic x;

            endmodule: m
        """.trimIndent()
        val builder = SvSourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }
}
