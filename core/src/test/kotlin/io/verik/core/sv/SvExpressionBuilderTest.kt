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
import io.verik.core.assert.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvExpressionBuilderTest {

    @Test
    fun `callable simple`() {
        val expression = SvExpressionCallable(FileLine(), SvExpressionIdentifier(FileLine(), "\$finish"), listOf())
        assertStringEquals("\$finish", expression.build())
    }

    @Test
    fun `callable with arguments`() {
        val expression = SvExpressionCallable(FileLine(), SvExpressionIdentifier(FileLine(), "\$finish"),
                listOf(SvExpressionLiteral(FileLine(), "0")))
        assertStringEquals("\$finish(0)", expression.build())
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.BASSIGN, listOf(
                SvExpressionLiteral(FileLine(), "x"),
                SvExpressionLiteral(FileLine(), "y")
        ))
        assertStringEquals("x = y", expression.build())
    }

    @Test
    fun `arithmetic add`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.ADD, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionIdentifier(FileLine(), "y")
        ))
        assertStringEquals("x + y", expression.build())
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.ADD, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionOperator(FileLine(), SvOperatorType.MUL, listOf(
                        SvExpressionIdentifier(FileLine(), "y"),
                        SvExpressionIdentifier(FileLine(), "z")
                ))
        ))
        assertStringEquals("x + y * z", expression.build())
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.MUL, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionOperator(FileLine(), SvOperatorType.ADD, listOf(
                        SvExpressionIdentifier(FileLine(), "y"),
                        SvExpressionIdentifier(FileLine(), "z")
                ))
        ))
        assertStringEquals("x * (y + z)", expression.build())
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.ADD, listOf(
                SvExpressionOperator(FileLine(), SvOperatorType.SUB, listOf(
                        SvExpressionIdentifier(FileLine(), "x"),
                        SvExpressionIdentifier(FileLine(), "y")
                )),
                SvExpressionIdentifier(FileLine(), "z")
        ))
        assertStringEquals("x - y + z", expression.build())
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.SUB, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionOperator(FileLine(), SvOperatorType.ADD, listOf(
                        SvExpressionIdentifier(FileLine(), "y"),
                        SvExpressionIdentifier(FileLine(), "z")
                ))
        ))
        assertStringEquals("x - (y + z)", expression.build())
    }

    @Test
    fun `conditional simple`() {
        val expression = SvExpressionOperator(FileLine(), SvOperatorType.IF, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionLiteral(FileLine(), "1"),
                SvExpressionLiteral(FileLine(), "0")
        ))
        assertStringEquals("x ? 1 : 0", expression.build())
    }

    @Test
    fun identifier() {
        val expression = SvExpressionIdentifier(FileLine(), "x")
        assertStringEquals("x", expression.build())
    }

    @Test
    fun `literal zero`() {
        val expression = SvExpressionLiteral(FileLine(), "0")
        assertStringEquals("0", expression.build())
    }
}