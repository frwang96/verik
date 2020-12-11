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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.KtUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.TYPE_INT

internal class KtParserExpressionPrimaryTest {

    @Test
    fun `parenthesized expression`() {
        val expression = KtUtil.parseExpression("(x)")
        assertEquals(KtExpressionProperty(Line(1), null, "x", null, null), expression)
    }

    @Test
    fun `if expression`() {
        val expression = KtUtil.parseExpression("if (x) y")
        val expected = KtExpressionOperator(
                Line(1),
                null,
                OPERATOR_IF,
                null,
                listOf(KtExpressionProperty(Line(1), null, "x", null, null)),
                listOf(KtBlock(
                        Line(1),
                        Symbol(1, 1, 1),
                        listOf(),
                        listOf(KtStatementExpression.wrapProperty(Line(1), null, "y", null, null))
                ))
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `if else expression`() {
        val expression = KtUtil.parseExpression("if (x) 0 else 1")
        val expected = KtExpressionOperator(
                Line(1),
                null,
                OPERATOR_IF_ELSE,
                null,
                listOf(KtExpressionProperty(Line(1), null, "x", null, null)),
                listOf(
                        KtBlock(
                                Line(1),
                                Symbol(1, 1, 1),
                                listOf(),
                                listOf(KtStatementExpression.wrapLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0)))
                        ),
                        KtBlock(
                                Line(1),
                                Symbol(1, 1, 2),
                                listOf(),
                                listOf(KtStatementExpression.wrapLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(1)))
                        )
                )
        )
        assertEquals (expected, expression)
    }

    @Test
    fun `when expression`() {
        val expression = KtUtil.parseExpression("""
            when {
                0 -> {}
            }
        """.trimIndent())
        val expected = KtExpressionOperator(
                Line(2),
                null,
                OPERATOR_IF,
                null,
                listOf(KtExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))),
                listOf(KtBlock(Line(2), Symbol(1, 1, 1), listOf(), listOf()))
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `when else expression`() {
        val expression = KtUtil.parseExpression("""
            when {
                0 -> {}
                else -> {}
            }
        """.trimIndent())
        val expected = KtExpressionOperator(
                Line(2),
                null,
                OPERATOR_IF_ELSE,
                null,
                listOf(KtExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))),
                listOf(
                        KtBlock(Line(2), Symbol(1, 1, 1), listOf(), listOf()),
                        KtBlock(Line(3), Symbol(1, 1, 2), listOf(), listOf())
                )
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `when condition expression`() {
        val expression = KtUtil.parseExpression("""
            when (x) {
                0 -> {}
            }
        """.trimIndent())
        val expected = KtExpressionOperator(
                Line(2),
                null,
                OPERATOR_IF,
                null,
                listOf(KtExpressionFunction(
                        Line(2),
                        null,
                        "eq",
                        KtExpressionProperty(Line(1), null, "x", null, null),
                        listOf(KtExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))),
                        null
                )),
                listOf(KtBlock(Line(2), Symbol(1, 1, 1), listOf(), listOf()))
        )
        assertEquals(expected, expression)
    }
}
