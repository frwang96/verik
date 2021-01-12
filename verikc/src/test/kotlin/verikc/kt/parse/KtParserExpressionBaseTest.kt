/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.line

internal class KtParserExpressionBaseTest {

    @Test
    fun `disjunction expression`() {
        val expression = KtParseUtil.parseExpression("x \n || y")
        val expected = KtExpressionFunction(
            line(3),
            "||",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(4), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `conjunction expression`() {
        val expression = KtParseUtil.parseExpression("x && \n y")
        val expected = KtExpressionFunction(
            line(3),
            "&&",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(4), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val expression = KtParseUtil.parseExpression("x == y")
        val expected = KtExpressionFunction(
            line(3),
            "==",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `comparison expression`() {
        val expression = KtParseUtil.parseExpression("x < y")
        val expected = KtExpressionFunction(
            line(3),
            "<",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation in expression`() {
        val expression = KtParseUtil.parseExpression("x in y")
        val expected = KtExpressionFunction(
            line(3),
            "in",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation is expression`() {
        val expression = KtParseUtil.parseExpression("x is y")
        val expected = KtExpressionFunction(
            line(3),
            "is",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression function`() {
        val expression = KtParseUtil.parseExpression("x con y")
        val expected = KtExpressionFunction(
            line(3),
            "con",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression operator implicit parameter`() {
        val expression = KtParseUtil.parseExpression("x with {}")
        val expected = KtExpressionOperator(
            line(3),
            OPERATOR_WITH,
            KtExpressionProperty(line(3), "x", null),
            listOf(),
            listOf(
                KtBlock(
                    line(3),
                    Symbol(5),
                    listOf(KtProperty(line(3), "it", Symbol(6), MutabilityType.VAL, listOf(), null, null)),
                    listOf()
                )
            )
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression operator explicit parameter`() {
        val expression = KtParseUtil.parseExpression("x with { y -> 0 }")
        val expected = KtExpressionOperator(
            line(3),
            OPERATOR_WITH,
            KtExpressionProperty(line(3), "x", null),
            listOf(),
            listOf(
                KtBlock(
                    line(3),
                    Symbol(5),
                    listOf(KtProperty(line(3), "y", Symbol(6), MutabilityType.VAL, listOf(), null, null)),
                    listOf(KtStatementExpression.wrapLiteral(line(3), "0"))
                )
            )
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression operator illegal parameters`() {
        assertThrowsMessage<LineException>("wrong number of lambda parameters") {
            KtParseUtil.parseExpression("x with { y, z -> 0 }")
        }
    }

    @Test
    fun `additive operation expression`() {
        val expression = KtParseUtil.parseExpression("x + y")
        val expected = KtExpressionFunction(
            line(3),
            "+",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `multiplicative operation expression`() {
        val expression = KtParseUtil.parseExpression("x * y")
        val expected = KtExpressionFunction(
            line(3),
            "*",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `as expression`() {
        val expression = KtParseUtil.parseExpression("x as y")
        val expected = KtExpressionFunction(
            line(3),
            "as",
            KtExpressionProperty(line(3), "x", null),
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }
}
