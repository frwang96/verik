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
import verikc.base.symbol.Symbol
import verikc.line
import verikc.rs.table.RsFunctionEntry

internal class RsFunctionOverloadResolverTest {

    @Test
    fun `function match`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0), Symbol(1)),
            listOf(Symbol(0))
        )
        val functionEntry = RsFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            false,
            Symbol(0)
        )
        assertTrue(RsFunctionOverloadResolver.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `function no match`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0)),
        )
        val functionEntry = RsFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1)),
            false,
            Symbol(0)
        )
        assertFalse(RsFunctionOverloadResolver.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `functions match vararg`() {
        val argsParentSymbols = listOf(
            listOf(Symbol(0), Symbol(1)),
            listOf(Symbol(0)),
            listOf(Symbol(0))
        )
        val functionEntry = RsFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            true,
            Symbol(0)
        )
        assertTrue(RsFunctionOverloadResolver.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `functions match no vararg`() {
        val argsParentSymbols = listOf<List<Symbol>>()
        val functionEntry = RsFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1), Symbol(0)),
            true,
            Symbol(0)
        )
        assertFalse(RsFunctionOverloadResolver.matches(argsParentSymbols, functionEntry))
    }

    @Test
    fun `dominating function entry`() {
        val functionEntries = listOf(
            RsFunctionEntry(Symbol(2), "", listOf(Symbol(1)), false, Symbol(0)),
            RsFunctionEntry(Symbol(3), "", listOf(Symbol(0)), false, Symbol(0))
        )
        val functionsArgsParentSymbols = listOf(
            listOf(listOf(Symbol(1), Symbol(0))),
            listOf(listOf(Symbol(0)))
        )
        assertEquals(
            functionEntries[0],
            RsFunctionOverloadResolver.dominatingFunctionEntry(functionEntries, functionsArgsParentSymbols, line(0))
        )
    }


    @Test
    fun `function dominates`() {
        val functionEntry = RsFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1)),
            false,
            Symbol(0)
        )
        val functionArgsParentSymbols = listOf(listOf(Symbol(1), Symbol(0)))
        val comparisonFunctionEntry = RsFunctionEntry(
            Symbol(3),
            "",
            listOf(Symbol(0)),
            false,
            Symbol(0)
        )
        assertTrue(
            RsFunctionOverloadResolver.dominates(functionEntry, functionArgsParentSymbols, comparisonFunctionEntry)
        )
    }

    @Test
    fun `function does not dominate`() {
        val functionEntry = RsFunctionEntry(
            Symbol(3),
            "",
            listOf(Symbol(0)),
            false,
            Symbol(0)
        )
        val functionArgsParentSymbols = listOf(listOf(Symbol(0)))
        val comparisonFunctionEntry = RsFunctionEntry(
            Symbol(2),
            "",
            listOf(Symbol(1)),
            false,
            Symbol(0)
        )
        assertFalse(
            RsFunctionOverloadResolver.dominates(functionEntry, functionArgsParentSymbols, comparisonFunctionEntry)
        )
    }
}