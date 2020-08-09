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

import io.verik.core.SourceBuilder
import io.verik.core.al.AlRuleParser
import io.verik.core.assert.assertStringEquals
import io.verik.core.sv.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class VkBlockTest {

    @Test
    fun `parse put block simple`() {
        val rule = AlRuleParser.parseDeclaration("@put fun f() {}")
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(1, VkBlockType.PUT, listOf(), listOf())
        assertEquals(expected, block)
    }

    @Test
    fun `parse put block`() {
        val rule = AlRuleParser.parseDeclaration("""
            @put fun f() {
                x put y
            }
        """.trimIndent())
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expectedExpression = VkExpressionOperator(2, VkOperatorType.PUT, listOf(
                VkExpressionIdentifier(2, "x"),
                VkExpressionIdentifier(2, "y")
        ))
        val expected = VkBlock(1, VkBlockType.PUT, listOf(), listOf(VkStatement(2, expectedExpression)))
        assertEquals(expected, block)
    }

    @Test
    fun `parse reg block`() {
        val rule = AlRuleParser.parseDeclaration("""
            @reg fun f() {
                on (posedge(clk)) {
                    x reg y
                }
            }
        """.trimIndent())
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expectedExpression = VkExpressionOperator(3, VkOperatorType.REG, listOf(
                VkExpressionIdentifier(3, "x"),
                VkExpressionIdentifier(3, "y")
        ))
        val expected = VkBlock(1, VkBlockType.REG, listOf(VkSensitivityEntry(VkSensitivityType.POSEDGE, "clk")),
                listOf(VkStatement(3, expectedExpression)))
        assertEquals(expected, block)
    }

    @Test
    fun `parse initial block`() {
        val rule = AlRuleParser.parseDeclaration("@initial fun f() {}")
        val declaration = VkDeclaration(rule)
        val functionDeclaration = declaration as VkFunctionDeclaration
        val block = VkBlock(functionDeclaration)
        val expected = VkBlock(1, VkBlockType.INITIAL, listOf(), listOf())
        assertEquals(expected, block)
    }

    @Test
    fun `extract continuous assignment`() {
        val expression = VkExpressionOperator(0, VkOperatorType.PUT, listOf(
                VkExpressionIdentifier(0, "x"),
                VkExpressionIdentifier(0, "y")
        ))
        val block = VkBlock(0, VkBlockType.PUT, listOf(), listOf(VkStatement(0, expression)))
        val continuousAssignment = block.extractContinuousAssignment()
        val expected = SvContinuousAssignment(0, SvExpressionOperator(0, SvOperatorType.BASSIGN, listOf(
                SvExpressionIdentifier(0, "x"),
                SvExpressionIdentifier(0, "y")
        )))
        assertEquals(expected, continuousAssignment)
    }

    @Test
    fun `extract reg block`() {
        val block = VkBlock(0, VkBlockType.REG, listOf(VkSensitivityEntry(VkSensitivityType.POSEDGE, "clk")), listOf())
        val expected = SvBlock(0, SvBlockType.ALWAYS_FF, listOf(SvSensitivityEntry(SvSensitivityType.POSEDGE, "clk")), listOf())
        assertEquals(expected, block.extractBlock())
    }

    @Test
    fun `extract initial block`() {
        val block = VkBlock(0, VkBlockType.INITIAL, listOf(), listOf())
        val continuousAssignment = block.extractContinuousAssignment()
        assertNull(continuousAssignment)
        val expected = SvBlock(0, SvBlockType.INITIAL, listOf(), listOf())
        assertEquals(expected, block.extractBlock())
    }

    @Test
    fun `end to end continuous assignment`() {
        val rule = AlRuleParser.parseDeclaration("""
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