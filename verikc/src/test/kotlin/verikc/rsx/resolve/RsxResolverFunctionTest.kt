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

package verikc.rsx.resolve

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.symbol.Symbol
import verikc.line
import verikc.rsx.table.RsxFunctionEntry

internal class RsxResolverFunctionTest {

    @Test
    fun `match true`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0), Symbol(1)),
            listOf(Symbol(0))
        )
        val functionEntry = RsxFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            listOf(VALUE, VALUE),
            false,
            { null },
            VALUE
        )
        assertTrue(RsxResolverFunction.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `match false`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0)),
        )
        val functionEntry = RsxFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1)),
            listOf(VALUE),
            false,
            { null },
            VALUE
        )
        assertFalse(RsxResolverFunction.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `match vararg true`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0), Symbol(1)),
            listOf(Symbol(0)),
            listOf(Symbol(0))
        )
        val functionEntry = RsxFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            listOf(VALUE, VALUE),
            true,
            { null },
            VALUE
        )
        assertTrue(RsxResolverFunction.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `match vararg false`() {
        val argsParentSymbols = listOf<List<Symbol>>()
        val functionEntry = RsxFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            listOf(VALUE, VALUE),
            true,
            { null },
            VALUE
        )
        assertFalse(RsxResolverFunction.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `dominating function entry`() {
        val functionEntries = listOf(
            RsxFunctionEntry(Symbol(2), "", listOf(Symbol(1)), listOf(VALUE), false, { null }, VALUE),
            RsxFunctionEntry(Symbol(3), "", listOf(Symbol(0)), listOf(VALUE), false, { null }, VALUE)
        )
        val functionsArgsParentSymbols = listOf(
            listOf(listOf(Symbol(1), Symbol(0))),
            listOf(listOf(Symbol(0)))
        )
        assertEquals(
            functionEntries[0],
            RsxResolverFunction.dominatingFunctionEntry(functionEntries, functionsArgsParentSymbols, line(0))
        )
    }
}