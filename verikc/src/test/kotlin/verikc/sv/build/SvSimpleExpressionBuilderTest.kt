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

package verikc.sv.build

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.base.ast.Line
import verikc.sv.ast.*

internal class SvSimpleExpressionBuilderTest {

    @Test
    fun `function simple`() {
        val expression = SvExpressionFunction(
            Line(0),
            null,
            "f",
            listOf()
        )
        val expected = "f()"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `function with arguments`() {
        val expression = SvExpressionFunction(
            Line(0),
            null,
            "f",
            listOf(
                SvExpressionProperty(Line(0), null, "x"),
                SvExpressionProperty(Line(0), null, "y")
            )
        )
        val expected = "f(x, y)"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `assignment blocking`() {
        val expression = SvExpressionOperator(
            Line(0),
            SvExpressionProperty(Line(0), null, "x"),
            SvOperatorType.ASSIGN_BLOCKING,
            listOf(SvExpressionProperty(Line(0), null, "y"))
        )
        val expected = "x = y"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvExpressionOperator(
            Line(0),
            SvExpressionProperty(Line(0), null, "x"),
            SvOperatorType.ADD,
            listOf(
                SvExpressionOperator(
                    Line(0),
                    SvExpressionProperty(Line(0), null, "y"),
                    SvOperatorType.MUL,
                    listOf(SvExpressionProperty(Line(0), null, "z"))
                )
            )
        )
        val expected = "x + y * z"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvExpressionOperator(
            Line(0),
            SvExpressionProperty(Line(0), null, "x"),
            SvOperatorType.MUL,
            listOf(
                SvExpressionOperator(
                    Line(0),
                    SvExpressionProperty(Line(0), null, "y"),
                    SvOperatorType.ADD,
                    listOf(SvExpressionProperty(Line(0), null, "z"))
                )
            )
        )
        val expected = "x * (y + z)"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvExpressionOperator(
            Line(0),
            SvExpressionOperator(
                Line(0),
                SvExpressionProperty(Line(0), null, "x"),
                SvOperatorType.SUB,
                listOf(SvExpressionProperty(Line(0), null, "y"))
            ),
            SvOperatorType.ADD,
            listOf(SvExpressionProperty(Line(0), null, "z"))
        )
        val expected = "x - y + z"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvExpressionOperator(
            Line(0),
            SvExpressionProperty(Line(0), null, "x"),
            SvOperatorType.SUB,
            listOf(
                SvExpressionOperator(
                    Line(0),
                    SvExpressionProperty(Line(0), null, "y"),
                    SvOperatorType.ADD,
                    listOf(SvExpressionProperty(Line(0), null, "z"))
                )
            )
        )
        val expected = "x - (y + z)"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `if expression`() {
        val expression = SvExpressionOperator(
            Line(0),
            SvExpressionProperty(Line(0), null, "x"),
            SvOperatorType.IF,
            listOf(SvExpressionLiteral(Line(0), "1"), SvExpressionLiteral(Line(0), "0"))
        )
        val expected = "x ? 1 : 0"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }

    @Test
    fun `posedge expression`() {
        val expression = SvExpressionOperator(
            Line(0),
            null,
            SvOperatorType.POSEDGE,
            listOf(SvExpressionProperty(Line(0), null, "clk"))
        )
        val expected = "posedge clk"
        assertStringEquals(expected, SvSimpleExpressionBuilder.build(expression))
    }
}
