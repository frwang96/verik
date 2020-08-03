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

import io.verik.core.FileLine
import io.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvExpressionBuilderTest {

    @Test
    fun `callable simple`() {
        val expression = SvCallableExpression(FileLine(), SvIdentifierExpression(FileLine(), "\$finish"), listOf())
        assertStringEquals("\$finish", expression.build())
    }

    @Test
    fun `callable with arguments`() {
        val expression = SvCallableExpression(FileLine(), SvIdentifierExpression(FileLine(), "\$finish"),
                listOf(SvLiteralExpression(FileLine(), "0")))
        assertStringEquals("\$finish(0)", expression.build())
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(FileLine(), "x"),
                SvLiteralExpression(FileLine(), "y")
        ))
        assertStringEquals("x = y", expression.build())
    }

    @Test
    fun `arithmetic add`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.ADD, listOf(
                SvIdentifierExpression(FileLine(), "x"),
                SvIdentifierExpression(FileLine(), "y")
        ))
        assertStringEquals("x + y", expression.build())
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.ADD, listOf(
                SvIdentifierExpression(FileLine(), "x"),
                SvOperatorExpression(FileLine(), SvOperatorType.MUL, listOf(
                        SvIdentifierExpression(FileLine(), "y"),
                        SvIdentifierExpression(FileLine(), "z")
                ))
        ))
        assertStringEquals("x + y * z", expression.build())
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.MUL, listOf(
                SvIdentifierExpression(FileLine(), "x"),
                SvOperatorExpression(FileLine(), SvOperatorType.ADD, listOf(
                        SvIdentifierExpression(FileLine(), "y"),
                        SvIdentifierExpression(FileLine(), "z")
                ))
        ))
        assertStringEquals("x * (y + z)", expression.build())
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.ADD, listOf(
                SvOperatorExpression(FileLine(), SvOperatorType.SUB, listOf(
                        SvIdentifierExpression(FileLine(), "x"),
                        SvIdentifierExpression(FileLine(), "y")
                )),
                SvIdentifierExpression(FileLine(), "z")
        ))
        assertStringEquals("x - y + z", expression.build())
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.SUB, listOf(
                SvIdentifierExpression(FileLine(), "x"),
                SvOperatorExpression(FileLine(), SvOperatorType.ADD, listOf(
                        SvIdentifierExpression(FileLine(), "y"),
                        SvIdentifierExpression(FileLine(), "z")
                ))
        ))
        assertStringEquals("x - (y + z)", expression.build())
    }

    @Test
    fun `conditional simple`() {
        val expression = SvOperatorExpression(FileLine(), SvOperatorType.IF, listOf(
                SvIdentifierExpression(FileLine(), "x"),
                SvLiteralExpression(FileLine(), "1"),
                SvLiteralExpression(FileLine(), "0")
        ))
        assertStringEquals("x ? 1 : 0", expression.build())
    }

    @Test
    fun identifier() {
        val expression = SvIdentifierExpression(FileLine(), "x")
        assertStringEquals("x", expression.build())
    }

    @Test
    fun `literal zero`() {
        val expression = SvLiteralExpression(FileLine(), "0")
        assertStringEquals("0", expression.build())
    }
}