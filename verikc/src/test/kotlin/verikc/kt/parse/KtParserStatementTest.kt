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
import verikc.kt.KtxUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_DO_WHILE
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_WHILE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line

internal class KtParserStatementTest {

    @Test
    fun `declaration simple`() {
        val statement = KtxUtil.parseStatement("val x = 0")
        val expected = KtStatementDeclaration(
            KtPrimaryProperty(
                line(3),
                "x",
                Symbol(5),
                null,
                listOf(),
                KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0))
            )
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `assignment direct`() {
        val statement = KtxUtil.parseStatement("x = 0")
        val expected = KtStatementExpression.wrapFunction(
            line(3),
            null,
            "=",
            KtExpressionProperty(line(3), null, "x", null, null),
            listOf(KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `assignment direct with suffix`() {
        val statement = KtxUtil.parseStatement("(x.y) = 0")
        val expected = KtStatementExpression.wrapFunction(
            line(3),
            null,
            "=",
            KtExpressionProperty(line(3), null, "y", KtExpressionProperty(line(3), null, "x", null, null), null),
            listOf(KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `assignment add`() {
        val statement = KtxUtil.parseStatement("x += 0")
        val expressionFunction = KtExpressionFunction(
            line(3),
            null,
            "+",
            KtExpressionProperty(line(3), null, "x", null, null),
            listOf(KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        val expected = KtStatementExpression.wrapFunction(
            line(3),
            null,
            "=",
            KtExpressionProperty(line(3), null, "x", null, null),
            listOf(expressionFunction),
            null
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `loop for`() {
        val statement = KtxUtil.parseStatement("for (x in y) {}")
        val expected = KtStatementExpression(
            KtExpressionOperator(
                line(3),
                null,
                OPERATOR_FOR_EACH,
                null,
                listOf(KtExpressionProperty(line(3), null, "y", null, null)),
                listOf(
                    KtBlock(
                        line(3),
                        Symbol(5),
                        listOf(KtLambdaProperty(line(3), "x", Symbol(6), null)),
                        listOf()
                    )
                )
            )
        )
        assertEquals(expected, statement)
    }

    @Test
    fun `loop while`() {
        val statement = KtxUtil.parseStatement("while (x) {}")
        val expected = KtStatementExpression(
            KtExpressionOperator(
                line(3),
                null,
                OPERATOR_WHILE,
                null,
                listOf(KtExpressionProperty(line(3), null, "x", null, null)),
                listOf(
                    KtBlock(
                        line(3),
                        Symbol(5),
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
        val statement = KtxUtil.parseStatement("do {} while (x)")
        val expected = KtStatementExpression(
            KtExpressionOperator(
                line(3),
                null,
                OPERATOR_DO_WHILE,
                null,
                listOf(KtExpressionProperty(line(3), null, "x", null, null)),
                listOf(
                    KtBlock(
                        line(3),
                        Symbol(5),
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
        val statement = KtxUtil.parseStatement("0")
        val expected = KtStatementExpression(
            KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0))
        )
        assertEquals(expected, statement)
    }
}
