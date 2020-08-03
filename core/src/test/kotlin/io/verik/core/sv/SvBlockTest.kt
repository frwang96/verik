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
import io.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvBlockTest {

    @Test
    fun `always_comb block`() {
        val statement = SvExpressionStatement(FileLine(), SvOperatorExpression(FileLine(), SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(FileLine(), "x"),
                SvLiteralExpression(FileLine(), "y")
        )))
        val block = SvBlock(SvBlockType.ALWAYS_COMB, listOf(), listOf(statement), FileLine())
        val builder = SourceBuilder()
        block.build(builder)
        val expected = """
            always_comb begin
              x = y;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `always_ff block`() {
        val statement = SvExpressionStatement(FileLine(), SvOperatorExpression(FileLine(), SvOperatorType.NBASSIGN, listOf(
                SvLiteralExpression(FileLine(), "x"),
                SvLiteralExpression(FileLine(), "y")
        )))
        val sensitivityEntries = listOf(
                SvSensitivityEntry(SvSensitivityType.POSEDGE, "clk"),
                SvSensitivityEntry(SvSensitivityType.POSEDGE, "reset")
        )
        val block = SvBlock(SvBlockType.ALWAYS_FF, sensitivityEntries, listOf(statement), FileLine())
        val builder = SourceBuilder()
        block.build(builder)
        val expected = """
            always_ff @(posedge clk or posedge reset) begin
              x <= y;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `initial block`() {
        val statement = SvExpressionStatement(FileLine(), SvOperatorExpression(FileLine(), SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(FileLine(), "x"),
                SvLiteralExpression(FileLine(), "y")
        )))
        val block = SvBlock(SvBlockType.INITIAL, listOf(), listOf(statement), FileLine())
        val builder = SourceBuilder()
        block.build(builder)
        val expected = """
            initial begin
              x = y;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}