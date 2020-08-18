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

package verik.core.kt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtExpressionParserTest {

    @Test
    fun `disjunction expression`() {
        val rule = AlRuleParser.parseExpression("x || y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.OR,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `conjunction expression`() {
        val rule = AlRuleParser.parseExpression("x && y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.AND,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `comparison expression`() {
        val rule = AlRuleParser.parseExpression("x < y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.LT,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation expression`() {
        val rule = AlRuleParser.parseExpression("x in y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.IN,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression`() {
        val rule = AlRuleParser.parseExpression("x con y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.INFIX_CON,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression with lambda`() {
        val rule = AlRuleParser.parseExpression("""
            x with {}
        """.trimIndent())
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.INFIX_WITH,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf(KtBlock(1, listOf()))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `range operation expression`() {
        val rule = AlRuleParser.parseExpression("x .. y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.RANGE,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `additive operation expression`() {
        val rule = AlRuleParser.parseExpression("x + y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.ADD,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `multiplicative operation expression`() {
        val rule = AlRuleParser.parseExpression("x * y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.MUL,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `prefix unary expression`() {
        val rule = AlRuleParser.parseExpression("!x")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.NOT,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix expression`() {
        val rule = AlRuleParser.parseExpression("x[0]")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.GET,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionLiteral(1, TYPE_INT, false, 1, 0)),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix expression`() {
        val rule = AlRuleParser.parseExpression("x.y")
        val expression = KtExpression(rule)
        val expected = KtExpressionProperty(
                1,
                null,
                "y",
                KtExpressionProperty(1, null, "x", null, null),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression`() {
        val rule = AlRuleParser.parseExpression("x()")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                null,
                "x",
                null,
                listOf(),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression with lambda`() {
        val rule = AlRuleParser.parseExpression("on() {}")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.LAMBDA_ON,
                null,
                listOf(),
                listOf(KtBlock(1, listOf()))
        )
        Assertions.assertEquals(expected, expression)
    }
}