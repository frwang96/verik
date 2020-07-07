package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvPortTest {

    @Test
    fun `input boolean`() {
        val port = SvPort(SvPortType.INPUT, listOf(), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("input", "logic", "", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }

    @Test
    fun `output byte`() {
        val port = SvPort(SvPortType.INPUT, listOf(SvRange(7, 0)), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("input", "logic", "[7:0]", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }
}