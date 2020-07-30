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

package io.verik.core.vk

import io.verik.core.LinePos
import io.verik.core.LinePosException
import io.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class VkExpressionParserTest {

    @Test
    fun `disjunction expression`() {
        val rule = KtRuleParser.parseExpression("x || y")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.OR, listOf(
                VkIdentifierExpression(LinePos(1, 1), "x"),
                VkIdentifierExpression(LinePos(1, 6), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val rule = KtRuleParser.parseExpression("x == y")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.EQ, listOf(
                VkIdentifierExpression(LinePos(1, 1), "x"),
                VkIdentifierExpression(LinePos(1, 6), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call`() {
        val rule = KtRuleParser.parseExpression("x until y")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.UNTIL, listOf(
                VkIdentifierExpression(LinePos(1, 1), "x"),
                VkIdentifierExpression(LinePos(1, 9), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call unrecognized`() {
        val rule = KtRuleParser.parseExpression("x to y")
        val exception = assertThrows<LinePosException> { VkExpression(rule) }
        assertEquals("(1, 1) infix operator to not recognized", exception.message)
    }

    @Test
    fun `prefix unary expression`() {
        val rule = KtRuleParser.parseExpression("!+x")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.NOT, listOf(
                VkOperatorExpression(LinePos(1, 1), VkOperatorType.UNARY_PLUS, listOf(
                        VkIdentifierExpression(LinePos(1, 3), "x")
                ))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `call suffix`() {
        val rule = KtRuleParser.parseExpression("x(y)")
        val expression = VkExpression(rule)
        val expected = VkCallableExpression(LinePos(1, 1),
                VkIdentifierExpression(LinePos(1, 1), "x"),
                listOf(VkIdentifierExpression(LinePos(1, 3), "y")))
        assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix`() {
        val rule = KtRuleParser.parseExpression("x[y]")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.GET, listOf(
                VkIdentifierExpression(LinePos(1, 1), "x"),
                VkIdentifierExpression(LinePos(1, 3), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix`() {
        val rule = KtRuleParser.parseExpression("x.y")
        val expression = VkExpression(rule)
        val expected = VkNavigationExpression(LinePos(1, 1), VkIdentifierExpression(LinePos(1, 1), "x"), "y")
        assertEquals(expected, expression)
    }

    @Test
    fun `parenthesized expression`() {
        val rule = KtRuleParser.parseExpression("(x)")
        val expression = VkExpression(rule)
        assertEquals(VkIdentifierExpression(LinePos(1, 2), "x"), expression)
    }

    @Test
    fun `identifier expression`() {
        val rule = KtRuleParser.parseExpression("x")
        val expression = VkExpression(rule)
        assertEquals(VkIdentifierExpression(LinePos(1, 1), "x"), expression)
    }

    @Test
    fun `literal expression int`() {
        val rule = KtRuleParser.parseExpression("0")
        val expression = VkExpression(rule)
        assertEquals(VkLiteralExpression(LinePos(1, 1), "0"), expression)
    }

    @Test
    fun `literal expression bool`() {
        val rule = KtRuleParser.parseExpression("false")
        val expression = VkExpression(rule)
        assertEquals(VkLiteralExpression(LinePos(1, 1), "0"), expression)
    }

    @Test
    fun `if expression`() {
        val rule = KtRuleParser.parseExpression("if (x) y")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.IF, listOf(
                VkIdentifierExpression(LinePos(1, 5), "x"),
                VkLambdaExpression(LinePos(1, 8), listOf(
                        VkStatement(VkIdentifierExpression(LinePos(1, 8), "y"), LinePos(1, 8))
                ))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `if else expression`() {
        val rule = KtRuleParser.parseExpression("if (x) 0 else 1")
        val expression = VkExpression(rule)
        val expected = VkOperatorExpression(LinePos(1, 1), VkOperatorType.IF_ELSE, listOf(
                VkIdentifierExpression(LinePos(1, 5), "x"),
                VkLambdaExpression(LinePos(1, 8), listOf(
                        VkStatement(VkLiteralExpression(LinePos(1, 8), "0"), LinePos(1, 8))
                )),
                VkLambdaExpression(LinePos(1, 15), listOf(
                        VkStatement(VkLiteralExpression(LinePos(1, 15), "1"), LinePos(1, 15))
                ))
        ))
        assertEquals(expected, expression)
    }
}