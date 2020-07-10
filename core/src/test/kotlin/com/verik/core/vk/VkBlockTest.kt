package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkBlockTest {

    @Test
    fun `parse put block simple`() {
        val rule = KtRuleParser.parseDeclaration("@put fun f() {}")
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(VkBlockType.PUT, "f", listOf(), LinePos(1, 6))
        assertEquals(expected, block)
    }

    @Test
    fun `parse put block`() {
        val rule = KtRuleParser.parseDeclaration("""
            @put fun f() {
                x put y
            }
        """.trimIndent())
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expectedExpression = VkOperatorExpression(LinePos(2, 5), VkOperatorType.PUT, listOf(
                VkIdentifierExpression(LinePos(2, 5), "x"),
                VkIdentifierExpression(LinePos(2, 11), "y")
        ))
        val expected = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expectedExpression, LinePos(2, 5))), LinePos(1, 6))
        assertEquals(expected, block)
    }

    @Test
    fun `parse initial block`() {
        val rule = KtRuleParser.parseDeclaration("@initial fun f() {}")
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(VkBlockType.INITIAL, "f", listOf(), LinePos(1, 10))
        assertEquals(expected, block)
    }

    @Test
    fun `extract continuous assignment`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.PUT, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkIdentifierExpression(LinePos.ZERO, "y")
        ))
        val block = VkBlock(VkBlockType.PUT, "f", listOf(VkStatement(expression, LinePos.ZERO)), LinePos.ZERO)
        val continuousAssignment = block.extractContinuousAssignment()
        val expected = SvContinuousAssignment(SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        )), LinePos.ZERO)
        assertEquals(expected, continuousAssignment)
    }

    @Test
    fun `extract initial block`() {
        val block = VkBlock(VkBlockType.INITIAL, "f", listOf(), LinePos.ZERO)
        val continuousAssignment = block.extractContinuousAssignment()
        assertNull(continuousAssignment)
        val expected = SvBlock(SvBlockType.INITIAL, listOf(), LinePos.ZERO)
        assertEquals(expected, block.extractBlock())
    }

    @Test
    fun `end to end continuous assignment`() {
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
}