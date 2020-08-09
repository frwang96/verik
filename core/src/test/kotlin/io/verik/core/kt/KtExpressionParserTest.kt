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

package io.verik.core.kt

import io.verik.core.al.AlRuleParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class KtExpressionParserTest {

    @Test
    fun `disjunction expression`() {
        val rule = AlRuleParser.parseExpression("x || y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.OR),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `conjunction expression`() {
        val rule = AlRuleParser.parseExpression("x && y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.AND),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val rule = AlRuleParser.parseExpression("x == y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.EQ),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `comparison expression`() {
        val rule = AlRuleParser.parseExpression("x < y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.LT),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation expression`() {
        val rule = AlRuleParser.parseExpression("x in y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.IN),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression`() {
        val rule = AlRuleParser.parseExpression("x con y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierNamed("con", true),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `range operation expression`() {
        val rule = AlRuleParser.parseExpression("x .. y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.RANGE),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `additive operation expression`() {
        val rule = AlRuleParser.parseExpression("x + y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.ADD),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `multiplicative operation expression`() {
        val rule = AlRuleParser.parseExpression("x * y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.MUL),
                listOf(KtExpressionProperty(1, null, "y"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `prefix unary expression`() {
        val rule = AlRuleParser.parseExpression("!x")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.NOT),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix expression`() {
        val rule = AlRuleParser.parseExpression("x[0]")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                1,
                KtExpressionProperty(1, null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.GET),
                listOf(KtExpressionLiteral(1, "0"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix expression`() {
        val rule = AlRuleParser.parseExpression("x.y")
        val expression = KtExpression(rule)
        val expected = KtExpressionProperty(
                1,
                KtExpressionProperty(1, null, "x"),
                "y"
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
                KtFunctionIdentifierNamed("x", false),
                listOf()
        )
        Assertions.assertEquals(expected, expression)
    }
}