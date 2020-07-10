package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvInstanceTest {

    @Test
    fun boolean() {
        val port = SvInstance(SvInstanceUsageType.REGULAR, listOf(), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("", "logic", "", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }

    @Test
    fun `boolean input`() {
        val port = SvInstance(SvInstanceUsageType.INPUT, listOf(), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("input", "logic", "", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }

    @Test
    fun `byte output`() {
        val port = SvInstance(SvInstanceUsageType.INPUT, listOf(SvRange(7, 0)), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("input", "logic", "[7:0]", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }
}