package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvBlockTest {

    @Test
    fun `initial block`() {
        val statement = SvExpressionStatement(LinePos.ZERO, SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        )))
        val block = SvBlock(SvBlockType.INITIAL, listOf(statement), LinePos.ZERO)
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