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
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verik.core.lang.LangSymbol.SCOPE_LANG
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class KtSymbolTableTest {

    @Test
    fun `resolve lang type`() {
        val symbolTable = KtUtil.getSymbolTable()
        assertEquals(
                TYPE_UNIT,
                symbolTable.resolveType("Unit", SCOPE_LANG, 0)
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
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(1, 1, 0), symbolTable)
        assertEquals(
                type.symbol,
                symbolTable.resolveType("_m", Symbol(1, 1, 0), 0)
        )
    }

    @Test
    fun `resolve type constructor`() {
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
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionFunction(0, null, "_m", null, listOf(), null)
        assertEquals(
                type.symbol,
                symbolTable.resolveFunction(expression, Symbol(1, 1, 0)).symbol
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
        val symbolTable = KtUtil.getSymbolTable()
        assertEquals(
                FUNCTION_FINISH,
                symbolTable.resolveFunction(function, SCOPE_LANG).symbol
        )
    }

    @Test
    fun `resolve lang function native add`() {
        val function = KtExpressionFunction(
                0,
                null,
                "+",
                KtExpressionLiteral(0, TYPE_INT, LiteralValue.fromIntImplicit(0)),
                listOf(KtExpressionLiteral(0, TYPE_INT, LiteralValue.fromIntImplicit(0))),
                null
        )
        val symbolTable = KtUtil.getSymbolTable()
        assertEquals(
                FUNCTION_NATIVE_ADD_INT_INT,
                symbolTable.resolveFunction(function, SCOPE_LANG).symbol
        )
    }

    @Test
    fun `resolve function`() {
        val function = KtDeclarationFunction(
                0,
                "f",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtFunctionBodyBlock(
                        "Unit",
                        KtBlock(1, Symbol(1, 1, 2), listOf(), listOf())
                ),
                null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(function, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionFunction(0, null, "f", null, listOf(), null)
        assertEquals(
                function.symbol,
                symbolTable.resolveFunction(expression, Symbol(1, 1, 0)).symbol
        )
    }

    @Test
    fun `resolve function with parameter`() {
        val function = KtDeclarationFunction(
                0,
                "f",
                Symbol(1, 1, 1),
                listOf(),
                listOf(KtDeclarationParameter(
                        0,
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        "_int",
                        KtUtil.EXPRESSION_NULL
                )),
                KtFunctionBodyBlock(
                        "Unit",
                        KtBlock(1, Symbol(1, 1, 1), listOf(), listOf())
                ),
                null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(function, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionFunction(
                0,
                null,
                "f",
                null,
                listOf(KtExpressionLiteral(0, TYPE_INT, LiteralValue.fromIntImplicit(0))),
                null
        )
        assertEquals(
                function.symbol,
                symbolTable.resolveFunction(expression, Symbol(1, 1, 0)).symbol
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
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(property, Symbol(1, 1, 0), symbolTable)
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
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(1, 1, 0), symbolTable)
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
                "_int",
                null
        )
        val function = KtDeclarationFunction(
                0,
                "f",
                Symbol(1, 1, 1),
                listOf(),
                listOf(property),
                KtFunctionBodyBlock(
                        "_int",
                        KtBlock(0, Symbol(1, 1, 1), listOf(), listOf())
                ),
                null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(function, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionProperty(0, null, "x", null, null)
        assertEquals(
                property.symbol,
                symbolTable.resolveProperty(expression, Symbol(1, 1, 1)).symbol
        )
    }

    @Test
    fun `resolve property with receiver`() {
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
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(1, 1, 0), symbolTable)
        val expression = KtExpressionProperty(
                0,
                null,
                "x",
                KtExpressionProperty(0, Symbol(1, 1, 1), "m", null, null),
                null
        )
        assertEquals(
                property.symbol,
                symbolTable.resolveProperty(expression, Symbol(1, 1, 0)).symbol
        )
    }
}