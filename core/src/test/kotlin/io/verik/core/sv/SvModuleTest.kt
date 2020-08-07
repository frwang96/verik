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

package io.verik.core.sv

import io.verik.core.FileLine
import io.verik.core.SourceBuilder
import io.verik.core.assert.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvModuleTest {

    @Test
    fun `module empty`() {
        val module = SvModule("m", listOf(), listOf(), listOf(), listOf(), listOf(), FileLine())
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
        val portDeclarations = listOf(SvInstanceDeclaration(SvInstancePortType.INPUT, 8, "a", listOf(), FileLine()))
        val module = SvModule("m", portDeclarations, listOf(), listOf(), listOf(), listOf(), FileLine())
        val expected = """
            module m (
              input logic [7:0] a
            );
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `module with instance`() {
        val instanceDeclarations = listOf(SvInstanceDeclaration(SvInstancePortType.NONE, 8, "a", listOf(), FileLine()))
        val module = SvModule("m", listOf(), instanceDeclarations, listOf(), listOf(), listOf(), FileLine())
        val expected = """
            module m;
              timeunit 1ns / 1ns;

              logic [7:0] a;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }
}