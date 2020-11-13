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
import verik.core.assertThrowsMessage
import verik.core.base.ast.LineException
import verik.core.base.ast.LiteralValue
import verik.core.base.ast.Symbol
import verik.core.kt.KtUtil
import verik.core.kt.ast.*
import verik.core.lang.LangSymbol.OPERATOR_WITH
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtParserExpressionTest {

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
    fun `equality expression`() {
        val expression = KtUtil.parseExpression("x == y")
        val expected = KtExpressionFunction(
                1,
                null,
                "==",
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
    fun `infix function expression operator implicit parameterse lr`() {
        val expression = KtUtil.parseExpression("x with {}")
        val expected = KtExpressionOperator(
                1,
                null,
                OPERATOR_WITH,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf(KtBlock(
                        1,
                        Symbol(1, 1, 1),
                        listOf(KtLambdaProperty(1, "it", Symbol(1, 1, 2), null)),
                        listOf()
                ))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression operator explicit parameter`() {
        val expression = KtUtil.parseExpression("x with { y -> 0 }")
        val expected = KtExpressionOperator(
                1,
                null,
                OPERATOR_WITH,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf(KtBlock(
                        1,
                        Symbol(1, 1, 1),
                        listOf(KtLambdaProperty(1, "y", Symbol(1, 1, 2), null)),
                        listOf(KtStatementExpression.wrapLiteral(1, TYPE_INT, LiteralValue.fromInt(0)))
                ))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression operator illegal parameters`() {
        assertThrowsMessage<LineException>("wrong number of lambda parameters") {
            KtUtil.parseExpression("x with { y, z -> 0 }")
        }
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