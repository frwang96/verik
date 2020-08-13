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

package io.verik.core.svx

import io.verik.core.assertStringEquals
import io.verik.core.main.SourceBuilder
import org.junit.jupiter.api.Test

internal class SvxModuleTest {

    @Test
    fun `module empty`() {
        val module = SvxModule(
                0,
                "m",
                listOf()
        )
        val expected = """
            module m;
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `module with port`() {
        val module = SvxModule(
                0,
                "m",
                listOf(SvxPort(
                        0,
                        SvxPortType.OUTPUT,
                        SvxType("logic", "[7:0]", ""),
                        "x"
                ))
        )
        val expected = """
            module m (
              output logic [7:0] x
            );
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }
}