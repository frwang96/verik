package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvAlignerTest {

    @Test
    fun alignment() {
        val lines = listOf(
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("##", "##"), LinePos.ZERO),
                SvAlignerLine(listOf("###", "###"), LinePos.ZERO)
        )
        val expected = """
            #   #
            ##  ##
            ### ###
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, "", "", builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun delimiter() {
        val lines = listOf(
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO)
        )
        val expected = """
            # #,
            # #,
            # #;
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, ",", ";", builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `column empty`() {
        val lines = listOf(
                SvAlignerLine(listOf("#", "", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "", "#"), LinePos.ZERO)
        )
        val expected = """
            # #
            # #
            # #
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, "", "", builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `column overflow`() {
        val lines = listOf(
                SvAlignerLine(listOf("#####", "", "", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("###", "", "#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "#", "#", "#"), LinePos.ZERO)
        )
        val expected = """
            ##### #
            ### # #
            # # # #
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, "", "", builder)
        assertStringEquals(expected, builder)
    }
}