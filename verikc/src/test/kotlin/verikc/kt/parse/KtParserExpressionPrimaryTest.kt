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
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line

internal class KtParserExpressionPrimaryTest {

    @Test
    fun `parenthesized expression`() {
        val string = """
            (x)
        """.trimIndent()
        val expected = KtExpressionProperty(line(3), null, "x", null, null)
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }

    @Test
    fun `if expression`() {
        val string = """
            if (x) y
        """.trimIndent()
        val expected = KtExpressionOperator(
            line(3),
            null,
            OPERATOR_IF,
            null,
            listOf(KtExpressionProperty(line(3), null, "x", null, null)),
            listOf(
                KtBlock(
                    line(3),
                    Symbol(5),
                    listOf(),
                    listOf(KtStatementExpression.wrapProperty(line(3), null, "y", null, null))
                )
            )
        )
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }

    @Test
    fun `if else expression`() {
        val string = """
            if (x) 0 else 1
        """.trimIndent()
        val expected = KtExpressionOperator(
            line(3),
            null,
            OPERATOR_IF_ELSE,
            null,
            listOf(KtExpressionProperty(line(3), null, "x", null, null)),
            listOf(
                KtBlock(
                    line(3),
                    Symbol(5),
                    listOf(),
                    listOf(KtStatementExpression.wrapLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0)))
                ),
                KtBlock(
                    line(3),
                    Symbol(6),
                    listOf(),
                    listOf(KtStatementExpression.wrapLiteral(line(3), TYPE_INT, LiteralValue.fromInt(1)))
                )
            )
        )
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }

    @Test
    fun `when expression`() {
        val string = """
            when {
                0 -> {}
            }
        """.trimIndent()
        val expected = KtExpressionOperator(
            line(4),
            null,
            OPERATOR_IF,
            null,
            listOf(KtExpressionLiteral(line(4), TYPE_INT, LiteralValue.fromInt(0))),
            listOf(KtBlock(line(4), Symbol(5), listOf(), listOf()))
        )
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }

    @Test
    fun `when else expression`() {
        val string = """
            when {
                0 -> {}
                else -> {}
            }
        """.trimIndent()
        val expected = KtExpressionOperator(
            line(4),
            null,
            OPERATOR_IF_ELSE,
            null,
            listOf(KtExpressionLiteral(line(4), TYPE_INT, LiteralValue.fromInt(0))),
            listOf(
                KtBlock(line(4), Symbol(5), listOf(), listOf()),
                KtBlock(line(5), Symbol(6), listOf(), listOf())
            )
        )
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }

    @Test
    fun `when condition expression`() {
        val string = """
            when (x) {
                0 -> {}
            }
        """.trimIndent()
        val expected = KtExpressionOperator(
            line(4),
            null,
            OPERATOR_IF,
            null,
            listOf(
                KtExpressionFunction(
                    line(4),
                    null,
                    "==",
                    KtExpressionProperty(line(3), null, "x", null, null),
                    listOf(KtExpressionLiteral(line(4), TYPE_INT, LiteralValue.fromInt(0))),
                    null
                )
            ),
            listOf(KtBlock(line(4), Symbol(5), listOf(), listOf()))
        )
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }
}
