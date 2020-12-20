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
import verikc.base.symbol.Symbol
import verikc.kt.KtUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_DO_WHILE
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_WHILE
import verikc.lang.LangSymbol.TYPE_INT

internal class KtParserStatementTest {

    @Test
    fun `declaration simple`() {
        val statement = KtUtil.parseStatement("val x = 0")
        val expected = KtStatementDeclaration(
            KtPrimaryProperty(
                Line(1),
                "x",
                Symbol(3),
                null,
                listOf(),
                KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))
            )
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `assignment direct`() {
        val statement = KtUtil.parseStatement("x = 0")
        val expected = KtStatementExpression.wrapFunction(
            Line(1),
            null,
            "=",
            KtExpressionProperty(Line(1), null, "x", null, null),
            listOf(KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `assignment direct with suffix`() {
        val statement = KtUtil.parseStatement("(x.y) = 0")
        val expected = KtStatementExpression.wrapFunction(
            Line(1),
            null,
            "=",
            KtExpressionProperty(Line(1), null, "y", KtExpressionProperty(Line(1), null, "x", null, null), null),
            listOf(KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `assignment add`() {
        val statement = KtUtil.parseStatement("x += 0")
        val expressionFunction = KtExpressionFunction(
            Line(1),
            null,
            "+",
            KtExpressionProperty(Line(1), null, "x", null, null),
            listOf(KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        val expected = KtStatementExpression.wrapFunction(
            Line(1),
            null,
            "=",
            KtExpressionProperty(Line(1), null, "x", null, null),
            listOf(expressionFunction),
            null
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `loop for`() {
        val statement = KtUtil.parseStatement("for (x in y) {}")
        val expected = KtStatementExpression(
            KtExpressionOperator(
                Line(1),
                null,
                OPERATOR_FOR_EACH,
                null,
                listOf(KtExpressionProperty(Line(1), null, "y", null, null)),
                listOf(
                    KtBlock(
                        Line(1),
                        Symbol(3),
                        listOf(KtLambdaProperty(Line(1), "x", Symbol(4), null)),
                        listOf()
                    )
                )
            )
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `loop while`() {
        val statement = KtUtil.parseStatement("while (x) {}")
        val expected = KtStatementExpression(
            KtExpressionOperator(
                Line(1),
                null,
                OPERATOR_WHILE,
                null,
                listOf(KtExpressionProperty(Line(1), null, "x", null, null)),
                listOf(
                    KtBlock(
                        Line(1),
                        Symbol(3),
                        listOf(),
                        listOf()
                    )
                )
            )
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `loop do while`() {
        val statement = KtUtil.parseStatement("do {} while (x)")
        val expected = KtStatementExpression(
            KtExpressionOperator(
                Line(1),
                null,
                OPERATOR_DO_WHILE,
                null,
                listOf(KtExpressionProperty(Line(1), null, "x", null, null)),
                listOf(
                    KtBlock(
                        Line(1),
                        Symbol(3),
                        listOf(),
                        listOf()
                    )
                )
            )
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `expression simple`() {
        val statement = KtUtil.parseStatement("0")
        val expected = KtStatementExpression(
            KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))
        )
        assertEquals(expected, statement)
    }
}
