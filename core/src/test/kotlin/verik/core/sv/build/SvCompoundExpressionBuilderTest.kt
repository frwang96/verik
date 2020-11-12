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

package verik.core.sv.build

import org.junit.jupiter.api.Test
import verik.core.assertStringEquals
import verik.core.sv.ast.*

internal class SvCompoundExpressionBuilderTest {

    @Test
    fun `forever block`() {
        val expression = SvExpressionControlBlock(
                0,
                SvControlBlockType.FOREVER,
                listOf(),
                listOf(SvBlock(0, listOf(SvStatementExpression(SvExpressionLiteral(0, "0")))))
        )
        val builder = SvSourceBuilder()
        expression.build(builder)
        val expected = """
            forever begin
              0;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `if block`() {
        val expression = SvExpressionControlBlock(
                0,
                SvControlBlockType.IF,
                listOf(SvExpressionProperty(0, null, "x")),
                listOf(SvBlock(0, listOf()))
        )
        val builder = SvSourceBuilder()
        expression.build(builder)
        val expected = """
            if (x) begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `if else block`() {
        val expression = SvExpressionControlBlock(
                0,
                SvControlBlockType.IF_ELSE,
                listOf(SvExpressionProperty(0, null, "x")),
                listOf(
                        SvBlock(0, listOf()),
                        SvBlock(0, listOf())
                )
        )
        val builder = SvSourceBuilder()
        expression.build(builder)
        val expected = """
            if (x) begin
            end
            else begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `if else chained block`() {
        val block = SvBlock(0, listOf(
                SvStatementExpression.wrapControlBlock(
                        0,
                        SvControlBlockType.IF_ELSE,
                        listOf(SvExpressionProperty(0, null, "y")),
                        listOf(
                                SvBlock(0, listOf()),
                                SvBlock(0, listOf())
                        )
                )
        ))
        val expression = SvExpressionControlBlock(
                0,
                SvControlBlockType.IF_ELSE,
                listOf(SvExpressionProperty(0, null, "x")),
                listOf(SvBlock(0, listOf()), block)
        )
        val builder = SvSourceBuilder()
        expression.build(builder)
        val expected = """
            if (x) begin
            end
            else if (y) begin
            end
            else begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}