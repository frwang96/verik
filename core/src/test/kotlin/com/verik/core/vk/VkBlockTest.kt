package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
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
            val expected = VkBlock(VkBlockType.PUT, "f", listOf(), LinePos(1, 6))
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
            val expectedExpression = VkFunctionExpression(LinePos(2, 5), "put", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos(2, 5), "x"),
                    VkIdentifierExpression(LinePos(2, 11), "y")
            ))
            val expected = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expectedExpression)), LinePos(1, 6))
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
            val builder = SourceBuilder()
            continuousAssignment!!.build(builder)
            assertStringEquals("assign x = y;", builder)
        }
    }
}