package com.verik.core.vk

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
        val expected = VkFunctionExpression("or", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression("x"),
                VkIdentifierExpression("y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val rule = KtRuleParser.parseExpression("x == y")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression("eq", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression("x"),
                VkIdentifierExpression("y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call expression`() {
        val rule = KtRuleParser.parseExpression("x until y")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression("until", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression("x"),
                VkIdentifierExpression("y")
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
        val expected = VkFunctionExpression("not", VkFunctionType.OPERATOR, listOf(
                VkFunctionExpression("unary_plus", VkFunctionType.OPERATOR, listOf(
                        VkIdentifierExpression("x")
                ))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `call suffix`() {
        val rule = KtRuleParser.parseExpression("x(y)")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression("invoke", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression("x"),
                VkIdentifierExpression("y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix`() {
        val rule = KtRuleParser.parseExpression("x[y]")
        val expression = VkExpression(rule)
        val expected = VkFunctionExpression("get", VkFunctionType.OPERATOR, listOf(
                VkIdentifierExpression("x"),
                VkIdentifierExpression("y")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix`() {
        val rule = KtRuleParser.parseExpression("x.y")
        val expression = VkExpression(rule)
        val expected = VkNavigationExpression(VkIdentifierExpression("x"), "y")
        assertEquals(expected, expression)
    }

    @Test
    fun `parenthesized expression`() {
        val rule = KtRuleParser.parseExpression("(x)")
        val expression = VkExpression(rule)
        assertEquals(VkIdentifierExpression("x"), expression)
    }

    @Test
    fun `identifier expression`() {
        val rule = KtRuleParser.parseExpression("x")
        val expression = VkExpression(rule)
        assertEquals(VkIdentifierExpression("x"), expression)
    }

    @Test
    fun `literal constant expression`() {
        val rule = KtRuleParser.parseExpression("0")
        val expression = VkExpression(rule)
        assertEquals(VkLiteralExpression("0"), expression)
    }
}