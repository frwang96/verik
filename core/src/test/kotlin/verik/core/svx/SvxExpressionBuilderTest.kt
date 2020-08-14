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

package verik.core.svx

import verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvxExpressionBuilderTest {

    @Test
    fun `function simple`() {
        val expression = SvxExpressionFunction(
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
        val expression = SvxExpressionFunction(
                0,
                null,
                "f",
                listOf(
                        SvxExpressionProperty(0, null, "x"),
                        SvxExpressionProperty(0, null, "y")
                )
        )
        val expected = "f(x, y)"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvxExpressionOperator(
                0,
                SvxExpressionProperty(0, null, "x"),
                SvxOperatorIdentifier.BLOCK_ASSIGN,
                listOf(SvxExpressionProperty(0, null, "y"))
        )
        val expected = "x = y"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvxExpressionOperator(
                0,
                SvxExpressionProperty(0, null, "x"),
                SvxOperatorIdentifier.ADD,
                listOf(SvxExpressionOperator(
                        0,
                        SvxExpressionProperty(0, null, "y"),
                        SvxOperatorIdentifier.MUL,
                        listOf(SvxExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x + y * z"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvxExpressionOperator(
                0,
                SvxExpressionProperty(0, null, "x"),
                SvxOperatorIdentifier.MUL,
                listOf(SvxExpressionOperator(
                        0,
                        SvxExpressionProperty(0, null, "y"),
                        SvxOperatorIdentifier.ADD,
                        listOf(SvxExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x * (y + z)"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvxExpressionOperator(
                0,
                SvxExpressionOperator(
                        0,
                        SvxExpressionProperty(0, null, "x"),
                        SvxOperatorIdentifier.SUB,
                        listOf(SvxExpressionProperty(0, null, "y"))
                ),
                SvxOperatorIdentifier.ADD,
                listOf(SvxExpressionProperty(0, null, "z"))
        )
        val expected = "x - y + z"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvxExpressionOperator(
                0,
                SvxExpressionProperty(0, null, "x"),
                SvxOperatorIdentifier.SUB,
                listOf(SvxExpressionOperator(
                        0,
                        SvxExpressionProperty(0, null, "y"),
                        SvxOperatorIdentifier.ADD,
                        listOf(SvxExpressionProperty(0, null, "z"))
                ))
        )
        val expected = "x - (y + z)"
        assertStringEquals(expected, expression.build())
    }

    @Test
    fun `if expression`() {
        val expression = SvxExpressionOperator(
                0,
                SvxExpressionProperty(0, null, "x"),
                SvxOperatorIdentifier.IF,
                listOf(
                        SvxExpressionLiteral(0, "1"),
                        SvxExpressionLiteral(0, "0")
                )
        )
        val expected = "x ? 1 : 0"
        assertStringEquals(expected, expression.build())
    }
}