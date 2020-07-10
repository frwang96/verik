package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvModuleTest {

    @Test
    fun `module empty`() {
        val module = SvModule("m", listOf(), listOf(), listOf(), listOf(), listOf(), LinePos.ZERO)
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
        val ports = listOf(SvInstance(SvInstanceUsageType.INPUT, listOf(SvRange(7, 0)), "a", listOf(), LinePos.ZERO))
        val module = SvModule("m", ports, listOf(), listOf(), listOf(), listOf(), LinePos.ZERO)
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

    @Test
    fun `module with instance`() {
        val instances = listOf(SvInstance(SvInstanceUsageType.REGULAR, listOf(SvRange(7, 0)), "a", listOf(), LinePos.ZERO))
        val module = SvModule("m", listOf(), instances, listOf(), listOf(), listOf(), LinePos.ZERO)
        val expected = """
            module m;
              timeunit 1ns / 1ns;

              logic [7:0] a;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        module.build(builder)
        assertStringEquals(expected, builder)
    }
}