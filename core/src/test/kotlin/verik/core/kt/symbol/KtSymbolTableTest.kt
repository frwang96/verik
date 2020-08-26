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

package verik.core.kt.symbol

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.SCOPE_LANG
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_DATA
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class KtSymbolTableTest {

    @Test
    fun `resolve lang type`() {
        val symbolTable = KtSymbolTable()
        assertEquals(
                TYPE_UNIT,
                symbolTable.resolveType("Unit", SCOPE_LANG, 0).symbol
        )
    }

    @Test
    fun `resolve lang type parents`() {
        val symbolTable = KtSymbolTable()
        assertEquals(
                listOf(TYPE_UINT, TYPE_DATA, TYPE_INSTANCE, TYPE_ANY),
                symbolTable.resolveType("_uint", SCOPE_LANG, 0).parents
        )
    }

    @Test
    fun `resolve type`() {
        val type = KtDeclarationType(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtConstructorInvocation(0, "_module", listOf(), null),
                null,
                listOf()
        )
        val symbolTable = KtSymbolTableBuilder.build(KtUtil.getSymbolContext())
        KtSymbolTableBuilder.buildDeclaration(type, Symbol(1, 1, 0), symbolTable)
        assertEquals(
                type.symbol,
                symbolTable.resolveType("_m", Symbol(1, 1, 0), 0).symbol
        )
    }

    @Test
    fun `resolve lang function finish`() {
        val function = KtExpressionFunction(
                0,
                null,
                "finish",
                null,
                listOf(),
                null
        )
        val symbolTable = KtSymbolTable()
        assertEquals(
                FUNCTION_FINISH,
                symbolTable.resolveFunction(function, SCOPE_LANG).symbol
        )
    }

    @Test
    fun `resolve property`() {
        val property = KtDeclarationPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 1),
                TYPE_INT,
                listOf(),
                KtUtil.EXPRESSION_NULL
        )
        val symbolTable = KtSymbolTableBuilder.build(KtUtil.getSymbolContext())
        KtSymbolTableBuilder.buildDeclaration(property, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionProperty(0, null, "x", null, null)
        assertEquals(
                property.symbol,
                symbolTable.resolveProperty(expression, Symbol(1, 1, 0)).symbol
        )
    }

    @Test
    fun `resolve property in type`() {
        val property = KtDeclarationPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 2),
                TYPE_INT,
                listOf(),
                KtUtil.EXPRESSION_NULL
        )
        val type = KtDeclarationType(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtConstructorInvocation(0, "_module", listOf(), null),
                listOf(),
                listOf(property)
        )
        val symbolTable = KtSymbolTableBuilder.build(KtUtil.getSymbolContext())
        KtSymbolTableBuilder.buildDeclaration(type, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionProperty(0, null, "x", null, null)
        assertEquals(
                property.symbol,
                symbolTable.resolveProperty(expression, Symbol(1, 1, 1)).symbol
        )
    }

    @Test
    fun `resolve property in function`() {
        val property = KtDeclarationParameter(
                0,
                "x",
                Symbol(1, 1, 2),
                TYPE_INT,
                "Int",
                null
        )
        val function = KtDeclarationFunction(
                0,
                "f",
                Symbol(1, 1, 1),
                listOf(),
                listOf(property),
                KtFunctionBodyBlock("Int", KtBlock(0, listOf(), listOf())),
                null
        )
        val symbolTable = KtSymbolTableBuilder.build(KtUtil.getSymbolContext())
        KtSymbolTableBuilder.buildDeclaration(function, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionProperty(0, null, "x", null, null)
        assertEquals(
                property.symbol,
                symbolTable.resolveProperty(expression, Symbol(1, 1, 1)).symbol
        )
    }
}