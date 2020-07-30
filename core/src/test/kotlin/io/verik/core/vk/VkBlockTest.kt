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

import io.verik.core.LinePos
import io.verik.core.SourceBuilder
import io.verik.core.assertStringEquals
import io.verik.core.kt.KtRuleParser
import io.verik.core.sv.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class VkBlockTest {

    @Test
    fun `parse put block simple`() {
        val rule = KtRuleParser.parseDeclaration("@put fun f() {}")
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(VkBlockType.PUT, listOf(), listOf(), LinePos(1, 6))
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
        val expected = VkBlock(VkBlockType.PUT, listOf(), listOf(VkStatement(expectedExpression, LinePos(2, 5))), LinePos(1, 6))
        assertEquals(expected, block)
    }

    @Test
    fun `parse reg block`() {
        val rule = KtRuleParser.parseDeclaration("""
            @reg fun f() {
                on (posedge(clk)) {
                    x reg y
                }
            }
        """.trimIndent())
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expectedExpression = VkOperatorExpression(LinePos(3, 9), VkOperatorType.REG, listOf(
                VkIdentifierExpression(LinePos(3, 9), "x"),
                VkIdentifierExpression(LinePos(3, 15), "y")
        ))
        val expected = VkBlock(VkBlockType.REG, listOf(VkSensitivityEntry(VkSensitivityType.POSEDGE, "clk")),
                listOf(VkStatement(expectedExpression, LinePos(3, 9))), LinePos(1, 6))
        assertEquals(expected, block)
    }

    @Test
    fun `parse initial block`() {
        val rule = KtRuleParser.parseDeclaration("@initial fun f() {}")
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(VkBlockType.INITIAL, listOf(), listOf(), LinePos(1, 10))
        assertEquals(expected, block)
    }

    @Test
    fun `extract continuous assignment`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.PUT, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkIdentifierExpression(LinePos.ZERO, "y")
        ))
        val block = VkBlock(VkBlockType.PUT, listOf(), listOf(VkStatement(expression, LinePos.ZERO)), LinePos.ZERO)
        val continuousAssignment = block.extractContinuousAssignment()
        val expected = SvContinuousAssignment(SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        )), LinePos.ZERO)
        assertEquals(expected, continuousAssignment)
    }

    @Test
    fun `extract reg block`() {
        val block = VkBlock(VkBlockType.REG, listOf(VkSensitivityEntry(VkSensitivityType.POSEDGE, "clk")), listOf(), LinePos.ZERO)
        val expected = SvBlock(SvBlockType.ALWAYS_FF, listOf(SvSensitivityEntry(SvSensitivityType.POSEDGE, "clk")), listOf(), LinePos.ZERO)
        assertEquals(expected, block.extractBlock())
    }

    @Test
    fun `extract initial block`() {
        val block = VkBlock(VkBlockType.INITIAL, listOf(), listOf(), LinePos.ZERO)
        val continuousAssignment = block.extractContinuousAssignment()
        assertNull(continuousAssignment)
        val expected = SvBlock(SvBlockType.INITIAL, listOf(), listOf(), LinePos.ZERO)
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