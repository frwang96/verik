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
import io.verik.core.sv.SvConnection
import io.verik.core.sv.SvIdentifierExpression
import io.verik.core.sv.SvModuleDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkModuleDeclarationTest {

    @Test
    fun `parse declaration`() {
        val rule = KtRuleParser.parseDeclaration("@comp val m = _m()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration(VkNamedType("_m"), "m", listOf(), LinePos(1, 7))
        assertEquals(expected, moduleDeclaration)
    }

    @Test
    fun `parse declaration implicit port`() {
        val rule = KtRuleParser.parseDeclaration("@comp val m = _m() with { clk }")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration(VkNamedType("_m"), "m", listOf(
                VkConnection("clk", VkIdentifierExpression(LinePos(1, 27), "clk"), LinePos(1, 27))
        ), LinePos(1, 7))
        assertEquals(expected, moduleDeclaration)
    }

    @Test
    fun `parse declaration explicit port`() {
        val rule = KtRuleParser.parseDeclaration("@comp val m = _m() with { it.clk con clk }")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration(VkNamedType("_m"), "m", listOf(
                VkConnection("clk", VkIdentifierExpression(LinePos(1, 38), "clk"), LinePos(1, 27))
        ), LinePos(1, 7))
        assertEquals(expected, moduleDeclaration)
    }

    @Test
    fun `extract module`() {
        val moduleDeclaration = VkModuleDeclaration(VkNamedType("_m"), "m0", listOf(), LinePos.ZERO)
        val expected = SvModuleDeclaration("m", "m0", listOf(), LinePos.ZERO)
        assertEquals(expected, moduleDeclaration.extract())
    }

    @Test
    fun `extract module with ports`() {
        val moduleDeclaration = VkModuleDeclaration(VkNamedType("_m"), "m0", listOf(
                VkConnection("clk", VkIdentifierExpression(LinePos.ZERO, "clk"), LinePos.ZERO),
                VkConnection("reset", VkIdentifierExpression(LinePos.ZERO, "reset"), LinePos.ZERO)
        ), LinePos.ZERO)
        val expected = SvModuleDeclaration("m", "m0", listOf(
                SvConnection("clk", SvIdentifierExpression(LinePos.ZERO, "clk"), LinePos.ZERO),
                SvConnection("reset", SvIdentifierExpression(LinePos.ZERO, "reset"), LinePos.ZERO)
        ), LinePos.ZERO)
        assertEquals(expected, moduleDeclaration.extract())
    }

    @Test
    fun `end to end module with ports`() {
        val declaration = VkDeclaration(KtRuleParser.parseDeclaration("""
            @comp val m0 = _m() with {
                it.clk con clk
                it.reset con reset
            }
        """.trimIndent()))
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val builder = SourceBuilder()
        moduleDeclaration.extract().build(builder)
        val expected = """
            m m0 (
              .clk(clk),
              .reset(reset)
            );
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}