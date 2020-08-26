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

package verik.core.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.Symbol
import verik.core.kt.KtDeclarationPrimaryProperty
import verik.core.kt.KtExpressionProperty
import verik.core.kt.KtUtil
import verik.core.kt.symbol.KtResolutionEntry
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.LangSymbol
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class KtResolverExpressionTest {

    @Test
    fun `function bool type`() {
        val string = "_bool()"
        val expression = KtUtil.resolveExpression(string)
        assertEquals(TYPE_BOOL, expression.type)
    }

    @Test
    fun `function uint type`() {
        val string = "_uint(1)"
        val expression = KtUtil.resolveExpression(string)
        assertEquals(TYPE_UINT, expression.type)
    }

    @Test
    fun `operator on`() {
        val string = "on () {}"
        val expression = KtUtil.resolveExpression(string)
        assertEquals(TYPE_UNIT, expression.type)
    }

    @Test
    fun `property simple`() {
        val string = "x"
        val expression = KtUtil.parseExpression(string)
        val property = KtDeclarationPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 1),
                TYPE_INT,
                listOf(),
                KtUtil.EXPRESSION_NULL
        )
        val symbolTable = KtSymbolTable()
        symbolTable.addFile(
                Symbol(1, 1, 0),
                listOf(KtResolutionEntry(listOf(Symbol(1, 1, 0))))
        )
        symbolTable.addProperty(property, Symbol(1, 1, 0), 0)
        KtResolverExpression.resolve(expression, Symbol(1, 1, 0), symbolTable)
        assertEquals(
                (expression as KtExpressionProperty).property,
                property.symbol
        )
    }

    @Test
    fun `string literal`() {
        val string = "\"0\""
        val expression = KtUtil.resolveExpression(string)
        assertEquals(LangSymbol.TYPE_STRING, expression.type)
    }

    @Test
    fun `literal bool`() {
        val string = "true"
        val expression = KtUtil.resolveExpression(string)
        assertEquals(TYPE_BOOL, expression.type)
    }

    @Test
    fun `literal int`() {
        val string = "0"
        val expression = KtUtil.resolveExpression(string)
        assertEquals(TYPE_INT, expression.type)
    }
}