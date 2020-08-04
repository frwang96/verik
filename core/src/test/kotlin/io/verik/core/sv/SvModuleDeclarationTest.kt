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

internal class SvModuleDeclarationTest {

    @Test
    fun `module empty`() {
        val moduleDeclaration = SvModuleDeclaration("m", "m0", listOf(), FileLine())
        val builder = SourceBuilder()
        moduleDeclaration.build(builder)
        assertStringEquals("m m0 ();", builder)
    }

    @Test
    fun `module with ports`() {
        val moduleDeclaration = SvModuleDeclaration("m", "m0", listOf(
                SvConnection("clk", SvIdentifierExpression(FileLine(), "clk"), FileLine()),
                SvConnection("reset", SvIdentifierExpression(FileLine(), "reset"), FileLine())
        ), FileLine())
        val expected = """
            m m0 (
              .clk(clk),
              .reset(reset)
            );
        """.trimIndent()
        val builder = SourceBuilder()
        moduleDeclaration.build(builder)
        assertStringEquals(expected, builder)
    }
}