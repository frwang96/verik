package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvModuleTest {

    @Test
    fun `empty module`() {
        val module = SvModule("m", listOf(), listOf(), listOf(), LinePos.ZERO)
        val expected = """
            module m;
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `module with port`() {
        val ports = listOf(SvPort(SvPortType.INPUT, listOf(SvRange(7, 0)), "a", listOf(), LinePos.ZERO))
        val module = SvModule("m", ports, listOf(), listOf(), LinePos.ZERO)
        val expected = """
            module m (
              input logic [7:0] a
            );
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }
}