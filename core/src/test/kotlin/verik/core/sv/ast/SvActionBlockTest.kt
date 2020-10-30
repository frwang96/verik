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

package verik.core.sv.ast

import org.junit.jupiter.api.Test
import verik.core.assertStringEquals
import verik.core.base.ast.ActionBlockType
import verik.core.sv.build.SvSourceBuilder

internal class SvActionBlockTest {

    @Test
    fun `run action block empty`() {
        val actionBlock = SvActionBlock(
                0,
                ActionBlockType.RUN,
                listOf(),
                SvBlock(0, listOf())
        )
        val builder = SvSourceBuilder()
        actionBlock.build(builder)
        val expected = """
            initial begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `run action block with statement`() {
        val actionBlock = SvActionBlock(
                0,
                ActionBlockType.RUN,
                listOf(),
                SvBlock(0, listOf(SvStatementExpression(SvExpressionLiteral(0, "0"))))
        )
        val builder = SvSourceBuilder()
        actionBlock.build(builder)
        val expected = """
            initial begin
              0;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `seq action block`() {
        val actionBlock = SvActionBlock(
                0,
                ActionBlockType.SEQ,
                listOf(SvExpressionOperator(
                        0,
                        null,
                        SvOperatorType.NEGEDGE,
                        listOf(SvExpressionProperty(0, null, "clk"))
                )),
                SvBlock(0, listOf())
        )
        val builder = SvSourceBuilder()
        actionBlock.build(builder)
        val expected = """
            always_ff @(negedge clk) begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}