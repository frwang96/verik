package com.verik.core.sv

import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvModuleTest {

    @Test
    fun `empty module`() {
        val module = SvModule("m", listOf(), listOf(), 0)
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
        val ports = listOf(SvPort(SvPortType.INPUT, SvRanges(listOf(Pair(7, 0))), "a", SvRanges(listOf()), 0))
        val module = SvModule("m", ports, listOf(), 0)
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