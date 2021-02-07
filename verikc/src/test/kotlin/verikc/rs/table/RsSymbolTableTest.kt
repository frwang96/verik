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

package verikc.rs.table

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
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsExpression
import verikc.rs.ast.RsExpressionFunction
import verikc.rs.ast.RsExpressionOperator
import verikc.rs.ast.RsExpressionProperty

internal class RsSymbolTableTest {

    @Test
    fun `resolve type unit`() {
        val symbolTable = RsResolveUtil.resolveSymbolTable("")
        assertEquals(
            RsTypeResult(TYPE_UNIT, false, TYPE_UNIT.toTypeGenerified()),
            symbolTable.resolveType("Unit", FILE_SYMBOL, Line(0))
        )
    }

    @Test
    fun `resolve function finish`() {
        val string = """
            finish()
        """.trimIndent()
        val expression = RsExpression(KtParseUtil.parseExpression(string)) as RsExpressionFunction
        val symbolTable = RsResolveUtil.resolveSymbolTable("")
        assertEquals(
            RsResolverResult(FUNCTION_FINISH, TYPE_UNIT.toTypeGenerified(), ExpressionClass.VALUE),
            symbolTable.resolveFunction(expression, FILE_SYMBOL)
        )
    }

    @Test
    fun `resolve function simple`() {
        val string = """
            fun f() {}
        """.trimIndent()
        val function = KtParseUtil.parseFunction(string)
        val symbolTable = RsResolveUtil.resolveSymbolTable(string)
        val expression = RsExpressionFunction(KtParseUtil.parseExpression("f()") as KtExpressionFunction)
        assertEquals(
            function.symbol,
            symbolTable.resolveFunction(expression, FILE_SYMBOL).symbol
        )
    }

    @Test
    fun `resolve property`() {
        val string = """
            val x = t_Int()
        """.trimIndent()
        val symbolTable = RsResolveUtil.resolveSymbolTable(string)
        val property = KtParseUtil.parseProperty(string)
        val expression = RsExpressionProperty(KtParseUtil.parseExpression("x") as KtExpressionProperty)
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
        val expression = RsExpression(KtParseUtil.parseExpression(string)) as RsExpressionOperator
        val symbolTable = RsResolveUtil.resolveSymbolTable("")
        assertEquals(
            RsResolverResult(OPERATOR_ON, TYPE_UNIT.toTypeGenerified(), ExpressionClass.VALUE),
            symbolTable.resolveOperator(expression)
        )
    }

    @Test
    fun `get parent type symbols data`() {
        val symbolTable = RsResolveUtil.resolveSymbolTable("")
        assertEquals(
            listOf(TYPE_DATA, TYPE_INSTANCE, TYPE_ANY),
            symbolTable.getParentTypeSymbols(TYPE_DATA, line(0))
        )
    }
}
