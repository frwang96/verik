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

internal class SvExpressionBuilderTest {

    @Test
    fun `function simple`() {
        val expression = SvExpressionFunction(
                0,
                null,
                "f",
                listOf()
        )
        val expected = "f()"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `function with arguments`() {
        val expression = SvExpressionFunction(
                0,
                null,
                "f",
                listOf(
                        SvExpressionProperty(0, null, "x"),
                        SvExpressionProperty(0, null, "y")
                )
        )
        val expected = "f(x, y)"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorType.BLOCK_ASSIGN,
                listOf(SvExpressionProperty(0, null, "y"))
        )
        val expected = "x = y"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorType.ADD,
                listOf(SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "y"),
                        SvOperatorType.MUL,
                        listOf(SvExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x + y * z"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorType.MUL,
                listOf(SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "y"),
                        SvOperatorType.ADD,
                        listOf(SvExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x * (y + z)"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "x"),
                        SvOperatorType.SUB,
                        listOf(SvExpressionProperty(0, null, "y"))
                ),
                SvOperatorType.ADD,
                listOf(SvExpressionProperty(0, null, "z"))
        )
        val expected = "x - y + z"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorType.SUB,
                listOf(SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "y"),
                        SvOperatorType.ADD,
                        listOf(SvExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x - (y + z)"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `if expression`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorType.IF,
                listOf(
                        SvExpressionLiteral(0, "1"),
                        SvExpressionLiteral(0, "0")
                )
        )
        val expected = "x ? 1 : 0"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }

    @Test
    fun `posedge expression`() {
        val expression = SvExpressionOperator(
                0,
                null,
                SvOperatorType.POSEDGE,
                listOf(SvExpressionProperty(0, null, "clk"))
        )
        val expected = "posedge clk"
        assertStringEquals(expected, SvExpressionBuilder.buildString(expression))
    }
}