package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkBlockTest {

    @Test
    fun `simple put block`() {
        val tree = KtTree.parseDeclaration("@put fun f() {}")
        val declaration = VkDeclaration(tree)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assert(VkBlock.isBlock(functionDeclaration))
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(VkBlockType.PUT, "f", listOf(), LinePos(0, 0))
        assertEquals(expected, block)
    }

    @Test
    fun `put block`() {
        val tree = KtTree.parseDeclaration("""
            @put fun f() {
                x put y
            }
        """.trimIndent())
        val declaration = VkDeclaration(tree)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assert(VkBlock.isBlock(functionDeclaration))
        val block = VkBlock(functionDeclaration)
        val expectedExpression = VkExpression(VkFunctionExpression("put", VkFunctionType.OPERATOR, listOf(
                VkExpression(VkIdentifierExpression("x")),
                VkExpression(VkIdentifierExpression("y"))
        )))
        val expected = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expectedExpression)), LinePos(0, 0))
        assertEquals(expected, block)
    }
}