package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvBlockTest {

    @Test
    fun `always_comb block`() {
        val statement = SvExpressionStatement(LinePos.ZERO, SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        )))
        val block = SvBlock(SvBlockType.ALWAYS_COMB, listOf(), listOf(statement), LinePos.ZERO)
        val builder = SourceBuilder()
        block.build(builder)
        val expected = """
            always_comb begin
              x = y;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `always_ff block`() {
        val statement = SvExpressionStatement(LinePos.ZERO, SvOperatorExpression(LinePos.ZERO, SvOperatorType.NBASSIGN, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        )))
        val sensitivityEntries = listOf(
                SvSensitivityEntry(SvSensitivityType.POSEDGE, "clk"),
                SvSensitivityEntry(SvSensitivityType.POSEDGE, "reset")
        )
        val block = SvBlock(SvBlockType.ALWAYS_FF, sensitivityEntries, listOf(statement), LinePos.ZERO)
        val builder = SourceBuilder()
        block.build(builder)
        val expected = """
            always_ff @(posedge clk or posedge reset) begin
              x <= y;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `initial block`() {
        val statement = SvExpressionStatement(LinePos.ZERO, SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        )))
        val block = SvBlock(SvBlockType.INITIAL, listOf(), listOf(statement), LinePos.ZERO)
        val builder = SourceBuilder()
        block.build(builder)
        val expected = """
            initial begin
              x = y;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}