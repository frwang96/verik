/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.core.sv

import io.verik.core.LinePos
import io.verik.core.SourceBuilder
import io.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

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