package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkBlockTest {

    @Nested
    inner class Parse {
        @Test
        fun `simple put block`() {
            val rule = KtRuleParser.parseDeclaration("@put fun f() {}")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            assert(VkBlock.isBlock(functionDeclaration))
            val block = VkBlock(functionDeclaration)
            val expected = VkBlock(VkBlockType.PUT, "f", listOf(), LinePos(0, 0))
            assertEquals(expected, block)
        }

        @Test
        fun `put block`() {
            val rule = KtRuleParser.parseDeclaration("""
                @put fun f() {
                    x put y
                }
            """.trimIndent())
            val declaration = VkDeclaration(rule)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            assert(VkBlock.isBlock(functionDeclaration))
            val block = VkBlock(functionDeclaration)
            val expectedExpression = VkFunctionExpression(LinePos(1, 0), "put", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos(1, 0), "x"),
                    VkIdentifierExpression(LinePos(1, 4), "y")
            ))
            val expected = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expectedExpression)), LinePos(0, 0))
            assertEquals(expected, block)
        }
    }

    @Nested
    inner class Extract {
        @Test
        fun `continuous assignment end to end`() {
            val rule = KtRuleParser.parseDeclaration("""
                @put fun f() {
                    x put y
                }
            """.trimIndent())
            val declaration = VkDeclaration(rule)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            assert(VkBlock.isBlock(functionDeclaration))
            val block = VkBlock(functionDeclaration)
            val continuousAssignment = block.extractContinuousAssignment()
            assertNotNull(continuousAssignment)
            assertEquals("assign x = y;", continuousAssignment!!.build())
        }
    }
}