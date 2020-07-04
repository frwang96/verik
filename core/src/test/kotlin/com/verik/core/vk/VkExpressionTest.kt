package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkExpressionTest {

    @Test
    fun `disjunction expression`() {
        val rule = KtRuleParser.parseExpression("x || y")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression(LinePos(0, 0), "or", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression(LinePos(0, 0), "x"),
                VkIdentifierExpression(LinePos(0, 3), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val rule = KtRuleParser.parseExpression("x == y")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression(LinePos(0, 0), "eq", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression(LinePos(0, 0), "x"),
                VkIdentifierExpression(LinePos(0, 3), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call expression`() {
        val rule = KtRuleParser.parseExpression("x until y")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression(LinePos(0, 0), "until", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression(LinePos(0, 0), "x"),
                VkIdentifierExpression(LinePos(0, 6), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `unrecognized infix function call expression`() {
        val rule = KtRuleParser.parseExpression("x to y")
        assertThrows<VkParseException> { VkExpression(rule) }
    }

    @Test
    fun `prefix unary expression`() {
        val rule = KtRuleParser.parseExpression("!+x")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression(LinePos(0, 0), "not", VkFunctionType.OPERATOR, listOf(
                VkFunctionExpression(LinePos(0, 0), "unary_plus", VkFunctionType.OPERATOR, listOf(
                        VkIdentifierExpression(LinePos(0, 2), "x")
                ))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `call suffix`() {
        val rule = KtRuleParser.parseExpression("x(y)")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression(LinePos(0, 0), "invoke", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression(LinePos(0, 0), "x"),
                VkIdentifierExpression(LinePos(0, 2), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix`() {
        val rule = KtRuleParser.parseExpression("x[y]")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression(LinePos(0, 0), "get", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression(LinePos(0, 0), "x"),
                VkIdentifierExpression(LinePos(0, 2), "y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix`() {
        val rule = KtRuleParser.parseExpression("x.y")
        val expression = VkExpression(rule)
        val expected = VkNavigationExpression(LinePos(0, 0), VkIdentifierExpression(LinePos(0, 0), "x"), "y")
        assertEquals(expected, expression)
    }

    @Test
    fun `parenthesized expression`() {
        val rule = KtRuleParser.parseExpression("(x)")
        val expression = VkExpression(rule)
        assertEquals(VkIdentifierExpression(LinePos(0, 1), "x"), expression)
    }

    @Test
    fun `identifier expression`() {
        val rule = KtRuleParser.parseExpression("x")
        val expression = VkExpression(rule)
        assertEquals(VkIdentifierExpression(LinePos(0, 0), "x"), expression)
    }

    @Test
    fun `literal constant expression`() {
        val rule = KtRuleParser.parseExpression("0")
        val expression = VkExpression(rule)
        assertEquals(VkLiteralExpression(LinePos(0, 0), "0"), expression)
    }
}