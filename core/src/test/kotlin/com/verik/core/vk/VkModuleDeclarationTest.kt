package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import com.verik.core.kt.*
import com.verik.core.sv.SvConnection
import com.verik.core.sv.SvIdentifierExpression
import com.verik.core.sv.SvModuleDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkModuleDeclarationTest {

    @Nested
    inner class Parse {

        @Test
        fun `empty declaration`() {
            val rule = KtRuleParser.parseDeclaration("@comp val m = _m()")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
            val expected = VkModuleDeclaration(VkNamedType("_m"), "m", listOf(), LinePos(1, 7))
            assertEquals(expected, moduleDeclaration)
        }

        @Test
        fun `with implicit port`() {
            val rule = KtRuleParser.parseDeclaration("@comp val m = _m() with { clk }")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
            val expected = VkModuleDeclaration(VkNamedType("_m"), "m", listOf(
                    VkConnection("clk", VkIdentifierExpression(LinePos(1, 27), "clk"), LinePos(1, 27))
            ), LinePos(1, 7))
            assertEquals(expected, moduleDeclaration)
        }

        @Test
        fun `with explicit port`() {
            val rule = KtRuleParser.parseDeclaration("@comp val m = _m() with { it.clk con clk }")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
            val expected = VkModuleDeclaration(VkNamedType("_m"), "m", listOf(
                    VkConnection("clk", VkIdentifierExpression(LinePos(1, 38), "clk"), LinePos(1, 27))
            ), LinePos(1, 7))
            assertEquals(expected, moduleDeclaration)
        }
    }

    @Nested
    inner class Extract {

        @Test
        fun `empty module`() {
            val moduleDeclaration = VkModuleDeclaration(VkNamedType("_m"), "m0", listOf(), LinePos.ZERO)
            val expected = SvModuleDeclaration("m", "m0", listOf(), LinePos.ZERO)
            assertEquals(expected, moduleDeclaration.extract())
        }

        @Test
        fun `module with ports`() {
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
        fun `module with ports end to end`() {
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
}