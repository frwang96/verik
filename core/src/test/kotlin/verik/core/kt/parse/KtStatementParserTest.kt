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

package verik.core.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_DO_WHILE
import verik.core.lang.LangSymbol.OPERATOR_FOR_EACH
import verik.core.lang.LangSymbol.OPERATOR_WHILE
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtStatementParserTest {

    @Test
    fun `declaration simple`() {
        val statement = KtUtil.parseStatement("val x = 0")
        val expected = KtStatementDeclaration(KtDeclarationPrimaryProperty(
                1,
                "x",
                Symbol(1, 1, 1),
                null,
                listOf(),
                KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(0))
        ))
        assertEquals(expected, statement)
    }

    @Test
    fun `loop for`() {
        val statement = KtUtil.parseStatement("for (x in y) {}")
        val expected = KtStatementExpression(KtExpressionOperator(
                1,
                null,
                OPERATOR_FOR_EACH,
                null,
                listOf(KtExpressionProperty(1, null, "y", null, null)),
                listOf(KtBlock(
                        1,
                        listOf(KtDeclarationLambdaParameter(1, "x", Symbol(1, 1, 1), null)),
                        listOf()
                ))
        ))
        assertEquals(expected, statement)
    }

    @Test
    fun `loop while`() {
        val statement = KtUtil.parseStatement("while (x) {}")
        val expected = KtStatementExpression(KtExpressionOperator(
                1,
                null,
                OPERATOR_WHILE,
                null,
                listOf(KtExpressionProperty(1, null, "x", null, null)),
                listOf(KtBlock(
                        1,
                        listOf(),
                        listOf()
                ))
        ))
        assertEquals(expected, statement)
    }

    @Test
    fun `loop do while`() {
        val statement = KtUtil.parseStatement("do {} while (x)")
        val expected = KtStatementExpression(KtExpressionOperator(
                1,
                null,
                OPERATOR_DO_WHILE,
                null,
                listOf(KtExpressionProperty(1, null, "x", null, null)),
                listOf(KtBlock(
                        1,
                        listOf(),
                        listOf()
                ))
        ))
        assertEquals(expected, statement)
    }

    @Test
    fun `expression simple`() {
        val statement = KtUtil.parseStatement("0")
        val expected = KtStatementExpression(KtExpressionLiteral(
                1,
                TYPE_INT,
                LiteralValue.fromIntImplicit(0)
        ))
        assertEquals(expected, statement)
    }
}