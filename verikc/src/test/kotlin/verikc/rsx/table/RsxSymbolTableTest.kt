/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.rsx.table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.FILE_SYMBOL
import verikc.base.ast.ExpressionClass
import verikc.base.ast.Line
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionFunction
import verikc.kt.ast.KtExpressionProperty
import verikc.lang.LangSymbol.FUNCTION_FINISH
import verikc.lang.LangSymbol.OPERATOR_ON
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.line
import verikc.rsx.RsxResolveUtil
import verikc.rsx.ast.RsxExpression
import verikc.rsx.ast.RsxExpressionFunction
import verikc.rsx.ast.RsxExpressionOperator
import verikc.rsx.ast.RsxExpressionProperty
import verikc.rsx.resolve.RsxResolverResult

internal class RsxSymbolTableTest {

    @Test
    fun `resolve type unit`() {
        val symbolTable = RsxResolveUtil.resolveSymbolTable("")
        assertEquals(
            TYPE_UNIT,
            symbolTable.resolveTypeSymbol("_unit", FILE_SYMBOL, Line(0))
        )
    }

    @Test
    fun `resolve function finish`() {
        val string = """
            finish()
        """.trimIndent()
        val expression = RsxExpression(KtParseUtil.parseExpression(string)) as RsxExpressionFunction
        val symbolTable = RsxResolveUtil.resolveSymbolTable("")
        assertEquals(
            RsxResolverResult(FUNCTION_FINISH, TYPE_UNIT.toTypeGenerified(), ExpressionClass.VALUE),
            symbolTable.resolveFunction(expression, FILE_SYMBOL)
        )
    }

    @Test
    fun `resolve function simple`() {
        val string = """
            fun f() {}
        """.trimIndent()
        val function = KtParseUtil.parseFunction(string)
        val symbolTable = RsxResolveUtil.resolveSymbolTable(string)
        val expression = RsxExpressionFunction(KtParseUtil.parseExpression("f()") as KtExpressionFunction)
        assertEquals(
            function.symbol,
            symbolTable.resolveFunction(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve property`() {
        val string = """
            val x = _int()
        """.trimIndent()
        val symbolTable = RsxResolveUtil.resolveSymbolTable(string)
        val property = KtParseUtil.parseProperty(string)
        val expression = RsxExpressionProperty(KtParseUtil.parseExpression("x") as KtExpressionProperty)
        assertEquals(
            property.symbol,
            symbolTable.resolveProperty(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve operator on`() {
        val string = """
            on () {}
        """.trimIndent()
        val expression = RsxExpression(KtParseUtil.parseExpression(string)) as RsxExpressionOperator
        val symbolTable = RsxResolveUtil.resolveSymbolTable("")
        assertEquals(
            RsxResolverResult(OPERATOR_ON, TYPE_UNIT.toTypeGenerified(), ExpressionClass.VALUE),
            symbolTable.resolveOperator(expression)
        )
    }

    @Test
    fun `get parent type symbols data`() {
        val symbolTable = RsxResolveUtil.resolveSymbolTable("")
        assertEquals(
            listOf(TYPE_DATA, TYPE_INSTANCE, TYPE_ANY),
            symbolTable.getParentTypeSymbols(TYPE_DATA, line(0))
        )
    }
}
