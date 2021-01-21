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

package verikc.rs.resolve

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.line
import verikc.rs.RsResolveUtil
import verikc.rs.table.RsFunctionEntryLang

internal class RsResolverFunctionUtilTest {

    @Test
    fun `match true`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0), Symbol(1)),
            listOf(Symbol(0))
        )
        val functionEntry = RsFunctionEntryLang(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            listOf(VALUE, VALUE),
            false,
            VALUE
        ) { null }
        assertTrue(RsResolverFunctionUtil.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `match false`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0)),
        )
        val functionEntry = RsFunctionEntryLang(
            Symbol(2),
            "",
            listOf(Symbol(1)),
            listOf(VALUE),
            false,
            VALUE
        ) { null }
        assertFalse(RsResolverFunctionUtil.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `match vararg true`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0), Symbol(1)),
            listOf(Symbol(0)),
            listOf(Symbol(0))
        )
        val functionEntry = RsFunctionEntryLang(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            listOf(VALUE, VALUE),
            true,
            VALUE
        ) { null }
        assertTrue(RsResolverFunctionUtil.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `match vararg false`() {
        val argsParentSymbols = listOf<List<Symbol>>()
        val functionEntry = RsFunctionEntryLang(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            listOf(VALUE, VALUE),
            true,
            VALUE
        ) { null }
        assertFalse(RsResolverFunctionUtil.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `dominating entry`() {
        val functionEntries = listOf(
            RsFunctionEntryLang(Symbol(2), "", listOf(TYPE_INSTANCE), listOf(VALUE), false, VALUE) { null },
            RsFunctionEntryLang(Symbol(3), "", listOf(TYPE_ANY), listOf(VALUE), false, VALUE) { null }
        )
        val symbolTable = RsResolveUtil.resolveSymbolTable("")
        assertEquals(
            functionEntries[0],
            RsResolverFunctionUtil.dominatingEntry(functionEntries, symbolTable, line(0))
        )
    }

    @Test
    fun `resolve sbit type class mismatch`() {
        val string = """
            t_Sbit(t_Int())
        """.trimIndent()
        val message = "type expression not permitted in argument 1 of function ${LangSymbol.FUNCTION_T_SBIT}"
        assertThrowsMessage<LineException>(message) {
            RsResolveUtil.resolveExpression("", string)
        }
    }

    @Test
    fun `resolve native add type class mismatch`() {
        val string = """
            t_Int() + 0
        """.trimIndent()
        val message = "type expression not permitted in receiver of function ${LangSymbol.FUNCTION_NATIVE_ADD_INT_INT}"
        assertThrowsMessage<LineException>(message) {
            RsResolveUtil.resolveExpression("", string)
        }
    }
}