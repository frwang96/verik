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

package verik.core.vk

import verik.core.main.SourceBuilder
import verik.core.al.AlRuleParser
import verik.core.assertStringEquals
import verik.core.sv.SvConnection
import verik.core.sv.SvExpressionIdentifier
import verik.core.sv.SvModuleDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkModuleDeclarationTest {

    @Test
    fun `parse declaration`() {
        val rule = AlRuleParser.parseDeclaration("@comp val m = _m()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration(1, VkNamedType("_m"), "m", listOf())
        assertEquals(expected, moduleDeclaration)
    }

    @Test
    fun `parse declaration implicit port`() {
        val rule = AlRuleParser.parseDeclaration("@comp val m = _m() with { clk }")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration(1, VkNamedType("_m"), "m", listOf(
                VkConnection(1, "clk", VkExpressionIdentifier(1, "clk"))
        ))
        assertEquals(expected, moduleDeclaration)
    }

    @Test
    fun `parse declaration explicit port`() {
        val rule = AlRuleParser.parseDeclaration("@comp val m = _m() with { it.clk con clk }")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration(1, VkNamedType("_m"), "m", listOf(
                VkConnection(1, "clk", VkExpressionIdentifier(1, "clk"))
        ))
        assertEquals(expected, moduleDeclaration)
    }

    @Test
    fun `extract module`() {
        val moduleDeclaration = VkModuleDeclaration(0, VkNamedType("_m"), "m0", listOf())
        val expected = SvModuleDeclaration(0, "m", "m0", listOf())
        assertEquals(expected, moduleDeclaration.extract())
    }

    @Test
    fun `extract module with ports`() {
        val moduleDeclaration = VkModuleDeclaration(0, VkNamedType("_m"), "m0", listOf(
                VkConnection(0, "clk", VkExpressionIdentifier(0, "clk")),
                VkConnection(0, "reset", VkExpressionIdentifier(0, "reset"))
        ))
        val expected = SvModuleDeclaration(0, "m", "m0", listOf(
                SvConnection(0, "clk", SvExpressionIdentifier(0, "clk")),
                SvConnection(0, "reset", SvExpressionIdentifier(0, "reset"))
        ))
        assertEquals(expected, moduleDeclaration.extract())
    }

    @Test
    fun `end to end module with ports`() {
        val declaration = VkDeclaration(AlRuleParser.parseDeclaration("""
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