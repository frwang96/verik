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

package verik.core.sv

import verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvExpressionBuilderTest {

    @Test
    fun `callable simple`() {
        val expression = SvExpressionCallable(0, SvExpressionIdentifier(0, "\$finish"), listOf())
        assertStringEquals("\$finish", expression.build())
    }

    @Test
    fun `callable with arguments`() {
        val expression = SvExpressionCallable(0, SvExpressionIdentifier(0, "\$finish"),
                listOf(SvExpressionLiteral(0, "0")))
        assertStringEquals("\$finish(0)", expression.build())
    }

    @Test
    fun `blocking assignment`() {
        val expression = SvExpressionOperator(0, SvOperatorType.BASSIGN, listOf(
                SvExpressionLiteral(0, "x"),
                SvExpressionLiteral(0, "y")
        ))
        assertStringEquals("x = y", expression.build())
    }

    @Test
    fun `arithmetic add`() {
        val expression = SvExpressionOperator(0, SvOperatorType.ADD, listOf(
                SvExpressionIdentifier(0, "x"),
                SvExpressionIdentifier(0, "y")
        ))
        assertStringEquals("x + y", expression.build())
    }

    @Test
    fun `arithmetic precedence ordered`() {
        val expression = SvExpressionOperator(0, SvOperatorType.ADD, listOf(
                SvExpressionIdentifier(0, "x"),
                SvExpressionOperator(0, SvOperatorType.MUL, listOf(
                        SvExpressionIdentifier(0, "y"),
                        SvExpressionIdentifier(0, "z")
                ))
        ))
        assertStringEquals("x + y * z", expression.build())
    }

    @Test
    fun `arithmetic precedence not ordered`() {
        val expression = SvExpressionOperator(0, SvOperatorType.MUL, listOf(
                SvExpressionIdentifier(0, "x"),
                SvExpressionOperator(0, SvOperatorType.ADD, listOf(
                        SvExpressionIdentifier(0, "y"),
                        SvExpressionIdentifier(0, "z")
                ))
        ))
        assertStringEquals("x * (y + z)", expression.build())
    }

    @Test
    fun `arithmetic precedence left to right`() {
        val expression = SvExpressionOperator(0, SvOperatorType.ADD, listOf(
                SvExpressionOperator(0, SvOperatorType.SUB, listOf(
                        SvExpressionIdentifier(0, "x"),
                        SvExpressionIdentifier(0, "y")
                )),
                SvExpressionIdentifier(0, "z")
        ))
        assertStringEquals("x - y + z", expression.build())
    }

    @Test
    fun `arithmetic precedence right to left`() {
        val expression = SvExpressionOperator(0, SvOperatorType.SUB, listOf(
                SvExpressionIdentifier(0, "x"),
                SvExpressionOperator(0, SvOperatorType.ADD, listOf(
                        SvExpressionIdentifier(0, "y"),
                        SvExpressionIdentifier(0, "z")
                ))
        ))
        assertStringEquals("x - (y + z)", expression.build())
    }

    @Test
    fun `conditional simple`() {
        val expression = SvExpressionOperator(0, SvOperatorType.IF, listOf(
                SvExpressionIdentifier(0, "x"),
                SvExpressionLiteral(0, "1"),
                SvExpressionLiteral(0, "0")
        ))
        assertStringEquals("x ? 1 : 0", expression.build())
    }

    @Test
    fun identifier() {
        val expression = SvExpressionIdentifier(0, "x")
        assertStringEquals("x", expression.build())
    }

    @Test
    fun `literal zero`() {
        val expression = SvExpressionLiteral(0, "0")
        assertStringEquals("0", expression.build())
    }
}