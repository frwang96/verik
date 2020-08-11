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

import io.verik.core.main.LineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkExpressionParserTest {

    @Test
    fun `disjunction expression`() {
        val rule = AlRuleParser.parseExpression("x || y")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.OR, listOf(
                VkExpressionIdentifier(1, "x"),
                VkExpressionIdentifier(1, "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val rule = AlRuleParser.parseExpression("x == y")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.EQ, listOf(
                VkExpressionIdentifier(1, "x"),
                VkExpressionIdentifier(1, "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call`() {
        val rule = AlRuleParser.parseExpression("x until y")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.UNTIL, listOf(
                VkExpressionIdentifier(1, "x"),
                VkExpressionIdentifier(1, "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call unrecognized`() {
        val rule = AlRuleParser.parseExpression("x to y")
        assertThrowsMessage<LineException>("infix operator to not recognized") {
            VkExpression(rule)
        }
    }

    @Test
    fun `prefix unary expression`() {
        val rule = AlRuleParser.parseExpression("!+x")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.NOT, listOf(
                VkExpressionOperator(1, VkOperatorType.UNARY_PLUS, listOf(
                        VkExpressionIdentifier(1, "x")
                ))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `call suffix`() {
        val rule = AlRuleParser.parseExpression("x(y)")
        val expression = VkExpression(rule)
        val expected = VkExpressionCallable(1,
                VkExpressionIdentifier(1, "x"),
                listOf(VkExpressionIdentifier(1, "y")))
        assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix`() {
        val rule = AlRuleParser.parseExpression("x[y]")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.GET, listOf(
                VkExpressionIdentifier(1, "x"),
                VkExpressionIdentifier(1, "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix`() {
        val rule = AlRuleParser.parseExpression("x.y")
        val expression = VkExpression(rule)
        val expected = VkExpressionNavigation(1, VkExpressionIdentifier(1, "x"), "y")
        assertEquals(expected, expression)
    }

    @Test
    fun `parenthesized expression`() {
        val rule = AlRuleParser.parseExpression("(x)")
        val expression = VkExpression(rule)
        assertEquals(VkExpressionIdentifier(1, "x"), expression)
    }

    @Test
    fun `identifier expression`() {
        val rule = AlRuleParser.parseExpression("x")
        val expression = VkExpression(rule)
        assertEquals(VkExpressionIdentifier(1, "x"), expression)
    }

    @Test
    fun `literal expression int`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = VkExpression(rule)
        assertEquals(VkExpressionLiteral(1, "0"), expression)
    }

    @Test
    fun `literal expression bool`() {
        val rule = AlRuleParser.parseExpression("false")
        val expression = VkExpression(rule)
        assertEquals(VkExpressionLiteral(1, "0"), expression)
    }

    @Test
    fun `if expression`() {
        val rule = AlRuleParser.parseExpression("if (x) y")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.IF, listOf(
                VkExpressionIdentifier(1, "x"),
                VkExpressionLambda(1, listOf(
                        VkStatement(1, VkExpressionIdentifier(1, "y"))
                ))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `if else expression`() {
        val rule = AlRuleParser.parseExpression("if (x) 0 else 1")
        val expression = VkExpression(rule)
        val expected = VkExpressionOperator(1, VkOperatorType.IF_ELSE, listOf(
                VkExpressionIdentifier(1, "x"),
                VkExpressionLambda(1, listOf(
                        VkStatement(1, VkExpressionLiteral(1, "0"))
                )),
                VkExpressionLambda(1, listOf(
                        VkStatement(1, VkExpressionLiteral(1, "1"))
                ))
        ))
        assertEquals(expected, expression)
    }
}