package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvFileTest {

    @Test
    fun `empty module`() {
        val file = SvFile(listOf(SvModule("m", listOf(), listOf(), listOf(), listOf(), LinePos.ZERO)))
        val expected = """
            module m;
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        file.build(builder)
        assertStringEquals(expected, builder)
    }
}