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

    @Test
    fun `if statement`() {
        val statement = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))), listOf())
        val builder = SourceBuilder()
        statement.build(builder)
        val expected = """
            if (x) begin
              0;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `if else statement`() {
        val statement = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "1"))))
        val builder = SourceBuilder()
        statement.build(builder)
        val expected = """
            if (x) begin
              0;
            end else begin
              1;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }

    @Test
    fun `if else nested statement`() {
        val statement = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))),
                listOf(SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "y"),
                        listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "1"))),
                        listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "2"))))))
        val builder = SourceBuilder()
        statement.build(builder)
        val expected = """
            if (x) begin
              0;
            end else if (y) begin
              1;
            end else begin
              2;
            end
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}