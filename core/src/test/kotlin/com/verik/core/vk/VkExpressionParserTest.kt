package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

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
        assertThrows<VkParseException> { VkExpression(rule) }
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
}