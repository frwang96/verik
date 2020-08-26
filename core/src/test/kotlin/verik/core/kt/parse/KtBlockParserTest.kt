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
import verik.core.lang.LangSymbol
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtBlockParserTest {

    @Test
    fun `block simple`() {
        val string = "forever { 0 }"
        val expected = KtExpressionOperator(
                1,
                null,
                LangSymbol.OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(KtBlock(
                        1,
                        listOf(),
                        listOf(KtStatementExpression.wrapLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(0)))
                ))
        )
        assertEquals(expected, KtUtil.parseExpression(string))
    }

    @Test
    fun `block with parameter`() {
        val string = "forever { x -> 0 }"
        val expected = KtExpressionOperator(
                1,
                null,
                LangSymbol.OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(KtBlock(
                        1,
                        listOf(KtDeclarationLambdaProperty(1, "x", Symbol(1, 1, 1), null)),
                        listOf(KtStatementExpression.wrapLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(0)))
                ))
        )
        assertEquals(expected, KtUtil.parseExpression(string))
    }
}