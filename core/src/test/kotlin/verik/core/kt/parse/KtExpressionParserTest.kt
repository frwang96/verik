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

package verik.core.kt.parse

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_WITH

internal class KtExpressionParserTest {

    @Test
    fun `disjunction expression`() {
        val expression = KtUtil.parseExpression("x || y")
        val expected = KtExpressionFunction(
                1,
                null,
                "||",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `conjunction expression`() {
        val expression = KtUtil.parseExpression("x && y")
        val expected = KtExpressionFunction(
                1,
                null,
                "&&",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `comparison expression`() {
        val expression = KtUtil.parseExpression("x < y")
        val expected = KtExpressionFunction(
                1,
                null,
                "<",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation in expression`() {
        val expression = KtUtil.parseExpression("x in y")
        val expected = KtExpressionFunction(
                1,
                null,
                "in",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation is expression`() {
        val expression = KtUtil.parseExpression("x is y")
        val expected = KtExpressionFunction(
                1,
                null,
                "is",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression function`() {
        val expression = KtUtil.parseExpression("x con y")
        val expected = KtExpressionFunction(
                1,
                null,
                "con",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression operator`() {
        val expression = KtUtil.parseExpression("""
            x with {}
        """.trimIndent())
        val expected = KtExpressionOperator(
                1,
                null,
                OPERATOR_WITH,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf(KtBlock(1, listOf()))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `range operation expression`() {
        val expression = KtUtil.parseExpression("x .. y")
        val expected = KtExpressionFunction(
                1,
                null,
                "..",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `additive operation expression`() {
        val expression = KtUtil.parseExpression("x + y")
        val expected = KtExpressionFunction(
                1,
                null,
                "+",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `multiplicative operation expression`() {
        val expression = KtUtil.parseExpression("x * y")
        val expected = KtExpressionFunction(
                1,
                null,
                "*",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `as expression`() {
        val expression = KtUtil.parseExpression("x as y")
        val expected = KtExpressionFunction(
                1,
                null,
                "as",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                null
        )
        Assertions.assertEquals(expected, expression)
    }
}