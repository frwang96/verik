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

package verik.core.sv

import verik.core.assertStringEquals
import org.junit.jupiter.api.Test

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
        assertStringEquals(expected, expression.build())
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
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorIdentifier.BLOCK_ASSIGN,
                listOf(SvExpressionProperty(0, null, "y"))
        )
        val expected = "x = y"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorIdentifier.ADD,
                listOf(SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "y"),
                        SvOperatorIdentifier.MUL,
                        listOf(SvExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x + y * z"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorIdentifier.MUL,
                listOf(SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "y"),
                        SvOperatorIdentifier.ADD,
                        listOf(SvExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x * (y + z)"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "x"),
                        SvOperatorIdentifier.SUB,
                        listOf(SvExpressionProperty(0, null, "y"))
                ),
                SvOperatorIdentifier.ADD,
                listOf(SvExpressionProperty(0, null, "z"))
        )
        val expected = "x - y + z"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorIdentifier.SUB,
                listOf(SvExpressionOperator(
                        0,
                        SvExpressionProperty(0, null, "y"),
                        SvOperatorIdentifier.ADD,
                        listOf(SvExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x - (y + z)"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `if expression`() {
        val expression = SvExpressionOperator(
                0,
                SvExpressionProperty(0, null, "x"),
                SvOperatorIdentifier.IF,
                listOf(
                        SvExpressionLiteral(0, "1"),
                        SvExpressionLiteral(0, "0")
                )
        )
        val expected = "x ? 1 : 0"
        assertStringEquals(expected, expression.build())
    }
}