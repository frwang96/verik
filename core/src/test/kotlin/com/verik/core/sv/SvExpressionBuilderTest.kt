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

package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvExpressionBuilderTest {

    @Test
    fun `callable simple`() {
        val expression = SvCallableExpression(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "\$finish"), listOf())
        assertStringEquals("\$finish", expression.build())
    }

    @Test
    fun `callable with arguments`() {
        val expression = SvCallableExpression(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "\$finish"),
                listOf(SvLiteralExpression(LinePos.ZERO, "0")))
        assertStringEquals("\$finish(0)", expression.build())
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        ))
        assertStringEquals("x = y", expression.build())
    }

    @Test
    fun `arithmetic add`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        ))
        assertStringEquals("x + y", expression.build())
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvOperatorExpression(LinePos.ZERO, SvOperatorType.MUL, listOf(
                        SvIdentifierExpression(LinePos.ZERO, "y"),
                        SvIdentifierExpression(LinePos.ZERO, "z")
                ))
        ))
        assertStringEquals("x + y * z", expression.build())
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.MUL, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                        SvIdentifierExpression(LinePos.ZERO, "y"),
                        SvIdentifierExpression(LinePos.ZERO, "z")
                ))
        ))
        assertStringEquals("x * (y + z)", expression.build())
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                SvOperatorExpression(LinePos.ZERO, SvOperatorType.SUB, listOf(
                        SvIdentifierExpression(LinePos.ZERO, "x"),
                        SvIdentifierExpression(LinePos.ZERO, "y")
                )),
                SvIdentifierExpression(LinePos.ZERO, "z")
        ))
        assertStringEquals("x - y + z", expression.build())
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.SUB, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                        SvIdentifierExpression(LinePos.ZERO, "y"),
                        SvIdentifierExpression(LinePos.ZERO, "z")
                ))
        ))
        assertStringEquals("x - (y + z)", expression.build())
    }

    @Test
    fun `conditional simple`() {
        val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.IF, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "1"),
                SvLiteralExpression(LinePos.ZERO, "0")
        ))
        assertStringEquals("x ? 1 : 0", expression.build())
    }

    @Test
    fun identifier() {
        val expression = SvIdentifierExpression(LinePos.ZERO, "x")
        assertStringEquals("x", expression.build())
    }

    @Test
    fun `literal zero`() {
        val expression = SvLiteralExpression(LinePos.ZERO, "0")
        assertStringEquals("0", expression.build())
    }
}