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
import verikc.FILE_SYMBOL
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.kt.KtxUtil
import verikc.kt.ast.KtExpressionFunction
import verikc.kt.ast.KtExpressionProperty
import verikc.kt.ast.KtFunction
import verikc.kt.ast.KtType
import verikc.lang.LangSymbol.FUNCTION_FINISH
import verikc.lang.LangSymbol.TYPE_UNIT

internal class KtSymbolTableTest {

    @Test
    fun `resolve lang type`() {
        val symbolTable = KtxUtil.buildSymbolTable("")
        assertEquals(
            TYPE_UNIT,
            symbolTable.resolveType("_unit", FILE_SYMBOL, Line(0))
        )
    }

    @Test
    fun `resolve type`() {
        val string = """
            class _m: _module
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string)
        assertEquals(
            declaration.symbol,
            symbolTable.resolveType("_m", FILE_SYMBOL, Line(0))
        )
    }

    @Test
    fun `resolve type constructor function`() {
        val string = """
            class _m: _module
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string) as KtType
        val expression = KtxUtil.parseExpression("_m()") as KtExpressionFunction
        assertEquals(
            declaration.declarations[0].symbol,
            symbolTable.resolveFunction(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve lang function finish`() {
        val symbolTable = KtxUtil.buildSymbolTable("")
        val function = KtxUtil.parseExpression("finish()") as KtExpressionFunction
        assertEquals(
            FUNCTION_FINISH,
            symbolTable.resolveFunction(function, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve function`() {
        val string = """
            fun f() {}
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string)
        val expression = KtxUtil.parseExpression("f()") as KtExpressionFunction
        assertEquals(
            declaration.symbol,
            symbolTable.resolveFunction(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve function with parameter`() {
        val string = """
            fun f(x: _int) {}
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string)
        val expression = KtxUtil.parseExpression("f(0)") as KtExpressionFunction
        assertEquals(
            declaration.symbol,
            symbolTable.resolveFunction(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve property`() {
        val string = """
            val x = _int()
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string)
        val expression = KtxUtil.parseExpression("x") as KtExpressionProperty
        assertEquals(
            declaration.symbol,
            symbolTable.resolveProperty(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve property in type`() {
        val string = """
            class _m: _module {
                val x = _int()
            }
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string) as KtType
        val expression = KtxUtil.parseExpression("x") as KtExpressionProperty
        assertEquals(
            declaration.declarations[1].symbol,
            symbolTable.resolveProperty(expression, declaration.symbol).symbol
        )
    }

    @Test
    fun `resolve property in function`() {
        val string = """
            fun f(x: _int) {}
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val declaration = KtxUtil.parseDeclaration(string) as KtFunction
        val expression = KtxUtil.parseExpression("x") as KtExpressionProperty
        assertEquals(
            declaration.parameters[0].symbol,
            symbolTable.resolveProperty(expression, declaration.symbol).symbol
        )
    }

    @Test
    fun `resolve property with receiver`() {
        val string = """
            class _m: _module {
                val x = _int()
            }
        """.trimIndent()
        val symbolTable = KtxUtil.buildSymbolTable(string)
        val expression = KtxUtil.parseExpression("m.x") as KtExpressionProperty
        expression.receiver?.typeSymbol = Symbol(3)
        assertEquals(
            Symbol(6),
            symbolTable.resolveProperty(expression, FILE_SYMBOL).symbol
        )
    }
}
