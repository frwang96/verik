package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvStatementTest {

    @Test
    fun expression() {
        val statement = SvExpressionStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"))
        val builder = SourceBuilder()
        statement.build(builder)
        assertStringEquals("x;", builder)
    }

    @Test
    fun `loop forever`() {
        val statement = SvLoopStatement(LinePos.ZERO, "forever", listOf())
        val builder = SourceBuilder()
        statement.build(builder)
        val expected = """
            forever begin
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}