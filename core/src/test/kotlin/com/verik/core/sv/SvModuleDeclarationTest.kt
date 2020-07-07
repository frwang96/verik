package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvModuleDeclarationTest {

    @Test
    fun `empty module`() {
        val moduleDeclaration = SvModuleDeclaration("m", "m0", listOf(), LinePos.ZERO)
        val builder = SourceBuilder()
        moduleDeclaration.build(builder)
        assertStringEquals("m m0 ();", builder)
    }

    @Test
    fun `module with ports`() {
        val moduleDeclaration = SvModuleDeclaration("m", "m0", listOf(
                SvConnection("clk", SvIdentifierExpression(LinePos.ZERO, "clk"), LinePos.ZERO),
                SvConnection("reset", SvIdentifierExpression(LinePos.ZERO, "reset"), LinePos.ZERO)
        ), LinePos.ZERO)
        val expected = """
            m m0 (
              .clk(clk),
              .reset(reset)
            );
        """.trimIndent()
        val builder = SourceBuilder()
        moduleDeclaration.build(builder)
        assertStringEquals(expected, builder)
    }
}