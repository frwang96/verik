package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.*
import org.junit.jupiter.api.Assertions.*
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
            val functionDeclaration = declaration as VkFunctionDeclaration
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
            val functionDeclaration = declaration as VkFunctionDeclaration
            val block = VkBlock(functionDeclaration)
            val expectedExpression = VkFunctionExpression(LinePos(2, 5), "put", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos(2, 5), "x"),
                    VkIdentifierExpression(LinePos(2, 11), "y")
            ))
            val expected = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expectedExpression, LinePos(2, 5))), LinePos(1, 6))
            assertEquals(expected, block)
        }

        @Test
        fun `initial block`() {
            val rule = KtRuleParser.parseDeclaration("@initial fun f() {}")
            val declaration = VkDeclaration(rule)
            val functionDeclaration = declaration as VkFunctionDeclaration
            val block = VkBlock(functionDeclaration)
            val expected = VkBlock(VkBlockType.INITIAL, "f", listOf(), LinePos(1, 10))
            assertEquals(expected, block)
        }
    }

    @Nested
    inner class Extract {

        @Test
        fun `continuous assignment`() {
            val expression = VkFunctionExpression(LinePos.ZERO, "put", VkFunctionType.OPERATOR, listOf(
                    VkIdentifierExpression(LinePos.ZERO, "x"),
                    VkIdentifierExpression(LinePos.ZERO, "y")
            ))
            val block = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expression, LinePos.ZERO)), LinePos.ZERO)
            val continuousAssignment = block.extractContinuousAssignment()
            val expected = SvContinuousAssignment(SvFunctionExpression(LinePos.ZERO, "bassign", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvIdentifierExpression(LinePos.ZERO, "y")
            )), LinePos.ZERO)
            assertEquals(expected, continuousAssignment)
        }

        @Test
        fun `continuous assignment end to end`() {
            val rule = KtRuleParser.parseDeclaration("""
                @put fun f() {
                    x put y
                }
            """.trimIndent())
            val declaration = VkDeclaration(rule)
            val functionDeclaration = declaration as VkFunctionDeclaration
            val block = VkBlock(functionDeclaration)
            val continuousAssignment = block.extractContinuousAssignment()
            assertNotNull(continuousAssignment)
            val builder = SourceBuilder()
            continuousAssignment!!.build(builder)
            assertStringEquals("assign x = y;", builder)
        }

        @Test
        fun `initial block`() {
            val block = VkBlock(VkBlockType.INITIAL, "f", listOf(), LinePos.ZERO)
            val continuousAssignment = block.extractContinuousAssignment()
            assertNull(continuousAssignment)
            val expected = SvBlock(SvBlockType.INITIAL, listOf(), LinePos.ZERO)
            assertEquals(expected, block.extractBlock())
        }
    }
}