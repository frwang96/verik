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

package verikc.kt.symbol

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.KtUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.FUNCTION_CON
import verikc.lang.LangSymbol.FUNCTION_FINISH
import verikc.lang.LangSymbol.SCOPE_LANG
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UNIT

internal class KtSymbolTableTest {

    @Test
    fun `resolve lang type`() {
        val symbolTable = KtUtil.getSymbolTable()
        assertEquals(
            TYPE_UNIT,
            symbolTable.resolveType("Unit", SCOPE_LANG, Line(0))
        )
    }

    @Test
    fun `resolve type`() {
        val type = KtPrimaryType(
            Line(0),
            "_m",
            Symbol(3),
            listOf(),
            listOf(),
            listOf(),
            KtConstructorInvocation(Line(0), "_module", listOf(), null),
            KtConstructorFunction(Line(0), "_m", Symbol(4), listOf(), Symbol(3)),
            null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(2), symbolTable)
        assertEquals(
            type.symbol,
            symbolTable.resolveType("_m", Symbol(2), Line(0))
        )
    }

    @Test
    fun `resolve type constructor`() {
        val type = KtPrimaryType(
            Line(0),
            "_m",
            Symbol(3),
            listOf(),
            listOf(),
            listOf(),
            KtConstructorInvocation(Line(0), "_module", listOf(), null),
            KtConstructorFunction(Line(0), "_m", Symbol(4), listOf(), Symbol(3)),
            null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(2), symbolTable)
        val expression = KtExpressionFunction(Line(0), null, "_m", null, listOf(), null)
        assertEquals(
            type.constructorFunction.symbol,
            symbolTable.resolveFunction(expression, Symbol(2)).symbol
        )
    }

    @Test
    fun `resolve lang function finish`() {
        val function = KtExpressionFunction(
            Line(0),
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
    fun `resolve lang function con`() {
        val function = KtExpressionFunction(
            Line(0),
            null,
            "con",
            KtExpressionLiteral(Line(0), TYPE_INT, LiteralValue.fromInt(0)),
            listOf(KtExpressionLiteral(Line(0), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        val symbolTable = KtUtil.getSymbolTable()
        assertEquals(
            FUNCTION_CON,
            symbolTable.resolveFunction(function, SCOPE_LANG).symbol
        )
    }

    @Test
    fun `resolve function`() {
        val function = KtPrimaryFunction(
            Line(0),
            "f",
            Symbol(3),
            listOf(),
            null,
            listOf(),
            KtFunctionBodyBlock("Unit", KtBlock(Line(1), Symbol(4), listOf(), listOf()))
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(function, Symbol(2), symbolTable)
        val expression = KtExpressionFunction(Line(0), null, "f", null, listOf(), null)
        assertEquals(
            function.symbol,
            symbolTable.resolveFunction(expression, Symbol(2)).symbol
        )
    }

    @Test
    fun `resolve function with parameter`() {
        val function = KtPrimaryFunction(
            Line(0),
            "f",
            Symbol(3),
            listOf(
                KtParameterProperty(
                    Line(0),
                    "x",
                    Symbol(4),
                    null,
                    "_int",
                    KtUtil.EXPRESSION_NULL
                )
            ),
            null,
            listOf(),
            KtFunctionBodyBlock("Unit", KtBlock(Line(1), Symbol(5), listOf(), listOf()))
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(function, Symbol(2), symbolTable)
        val expression = KtExpressionFunction(
            Line(0),
            null,
            "f",
            null,
            listOf(KtExpressionLiteral(Line(0), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        assertEquals(
            function.symbol,
            symbolTable.resolveFunction(expression, Symbol(2)).symbol
        )
    }

    @Test
    fun `resolve property`() {
        val property = KtPrimaryProperty(
            Line(0),
            "x",
            Symbol(3),
            TYPE_INT,
            listOf(),
            KtUtil.EXPRESSION_NULL
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(property, Symbol(2), symbolTable)
        val expression = KtExpressionProperty(Line(0), null, "x", null, null)
        assertEquals(
            property.symbol,
            symbolTable.resolveProperty(expression, Symbol(2)).symbol
        )
    }

    @Test
    fun `resolve property in type`() {
        val property = KtPrimaryProperty(
            Line(0),
            "x",
            Symbol(5),
            TYPE_INT,
            listOf(),
            KtUtil.EXPRESSION_NULL
        )
        val type = KtPrimaryType(
            Line(0),
            "_m",
            Symbol(3),
            listOf(property),
            listOf(),
            listOf(),
            KtConstructorInvocation(Line(0), "_module", listOf(), null),
            KtConstructorFunction(Line(0), "_m", Symbol(4), listOf(), Symbol(3)),
            null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(2), symbolTable)
        val expression = KtExpressionProperty(Line(0), null, "x", null, null)
        assertEquals(
            property.symbol,
            symbolTable.resolveProperty(expression, Symbol(3)).symbol
        )
    }

    @Test
    fun `resolve property in function`() {
        val property = KtParameterProperty(
            Line(0),
            "x",
            Symbol(4),
            TYPE_INT,
            "_int",
            null
        )
        val function = KtPrimaryFunction(
            Line(0),
            "f",
            Symbol(3),
            listOf(property),
            null,
            listOf(),
            KtFunctionBodyBlock("_int", KtBlock(Line(0), Symbol(5), listOf(), listOf()))
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(function, Symbol(2), symbolTable)
        val expression = KtExpressionProperty(Line(0), null, "x", null, null)
        assertEquals(
            property.symbol,
            symbolTable.resolveProperty(expression, Symbol(3)).symbol
        )
    }

    @Test
    fun `resolve property with receiver`() {
        val property = KtPrimaryProperty(
            Line(0),
            "x",
            Symbol(5),
            TYPE_INT,
            listOf(),
            KtUtil.EXPRESSION_NULL
        )
        val type = KtPrimaryType(
            Line(0),
            "_m",
            Symbol(3),
            listOf(property),
            listOf(),
            listOf(),
            KtConstructorInvocation(Line(0), "_module", listOf(), null),
            KtConstructorFunction(Line(0), "_m", Symbol(4), listOf(), Symbol(3)),
            null
        )
        val symbolTable = KtUtil.getSymbolTable()
        KtUtil.resolveDeclaration(type, Symbol(2), symbolTable)
        val expression = KtExpressionProperty(
            Line(0),
            null,
            "x",
            KtExpressionProperty(Line(0), Symbol(3), "m", null, null),
            null
        )
        assertEquals(
            property.symbol,
            symbolTable.resolveProperty(expression, Symbol(2)).symbol
        )
    }
}
