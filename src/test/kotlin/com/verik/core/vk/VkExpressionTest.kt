package com.verik.core.vk

import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkExpressionTest {

    @Test
    fun `disjunction expression`() {
        val tree = KtTree.parseExpression("x || y")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkFunctionExpression("or", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkIdentifierExpression("x")),
                VkExpression(VkIdentifierExpression("y"))
        )))
        assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val tree = KtTree.parseExpression("x == y")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkFunctionExpression("eq", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkIdentifierExpression("x")),
                VkExpression(VkIdentifierExpression("y"))
        )))
        assertEquals(expected, expression)
    }

    @Test
    fun `infix function call expression`() {
        val tree = KtTree.parseExpression("x until y")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkFunctionExpression("until", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkIdentifierExpression("x")),
                VkExpression(VkIdentifierExpression("y"))
        )))
        assertEquals(expected, expression)
    }

    @Test
    fun `unrecognized infix function call expression`() {
        val tree = KtTree.parseExpression("x to y")
        assertThrows<VkParseException> { VkExpression(tree) }
    }

    @Test
    fun `prefix unary expression`() {
        val tree = KtTree.parseExpression("!+x")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkFunctionExpression("not", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkFunctionExpression("unary_plus", VkFunctionType.OPERATOR, listOf(
                        VkExpression(VkIdentifierExpression("x"))
                ))
        ))))
        assertEquals(expected, expression)
    }

    @Test
    fun `call suffix`() {
        val tree = KtTree.parseExpression("x(y)")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkFunctionExpression("invoke", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkIdentifierExpression("x")),
                VkExpression(VkIdentifierExpression("y"))
        )))
        assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix`() {
        val tree = KtTree.parseExpression("x[y]")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkFunctionExpression("get", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkIdentifierExpression("x")),
                VkExpression(VkIdentifierExpression("y"))
        )))
        assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix`() {
        val tree = KtTree.parseExpression("x.y")
        val expression = VkExpression(tree)
        val expected = VkExpression(VkNavigationExpression(VkExpression(VkIdentifierExpression("x")), "y"))
        assertEquals(expected, expression)
    }

    @Test
    fun `parenthesized expression`() {
        val tree = KtTree.parseExpression("(x)")
        val expression = VkExpression(tree)
        assertEquals(VkExpression(VkIdentifierExpression("x")), expression)
    }

    @Test
    fun `identifier expression`() {
        val tree = KtTree.parseExpression("x")
        val expression = VkExpression(tree)
        assertEquals(VkExpression(VkIdentifierExpression("x")), expression)
    }

    @Test
    fun `literal constant expression`() {
        val tree = KtTree.parseExpression("0")
        val expression = VkExpression(tree)
        assertEquals(VkExpression(VkLiteralExpression("0")), expression)
    }
}