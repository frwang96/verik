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
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionProperty
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsExpressionProperty

internal class RsSymbolTableTest {

    @Test
    fun `resolve property in type`() {
        val string = """
            class _m: _module() {
                val x = _int()
            }
        """.trimIndent()
        val symbolTable = RsResolveUtil.resolveSymbolTable(string)
        val type = KtParseUtil.parseType(string)
        val expression = RsExpressionProperty(KtParseUtil.parseExpression("x") as KtExpressionProperty)
        assertEquals(
            type.properties[0].symbol,
            symbolTable.resolveProperty(expression, type.symbol).symbol
        )
    }

    @Test
    fun `resolve property in function`() {
        val string = """
            fun f(x: _int) {}
        """.trimIndent()
        val symbolTable = RsResolveUtil.resolveSymbolTable(string)
        val function = KtParseUtil.parseFunction(string)
        val expression = RsExpressionProperty(KtParseUtil.parseExpression("x") as KtExpressionProperty)
        assertEquals(
            function.parameterProperties[0].symbol,
            symbolTable.resolveProperty(expression, function.symbol).symbol
        )
    }

    @Test
    fun `resolve property with receiver`() {
        val string = """
            class _m: _module() {
                val x = _int()
            }
        """.trimIndent()
        val symbolTable = RsResolveUtil.resolveSymbolTable(string)
        val expression = RsExpressionProperty(KtParseUtil.parseExpression("m.x") as KtExpressionProperty)
        expression.receiver?.typeSymbol = Symbol(3)
        assertEquals(
            Symbol(6),
            symbolTable.resolveProperty(expression, FILE_SYMBOL).symbol
        )
    }
}
