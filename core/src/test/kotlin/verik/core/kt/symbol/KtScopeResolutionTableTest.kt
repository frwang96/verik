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
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.Symbol

internal class KtScopeResolutionTableTest  {

    @Test
    fun `parent is file`() {
        val scopeResolutionTable = KtScopeResolutionTable()
        scopeResolutionTable.addFile(
                Symbol(1, 1, 0),
                listOf(Symbol(1, 0, 0))
        )
        assertEquals(
                listOf(Symbol(1, 0, 0)),
                scopeResolutionTable.resolutionEntries(Symbol(1, 1, 0), 0)
        )
    }

    @Test
    fun `parent is declaration`() {
        val scopeResolutionTable = KtScopeResolutionTable()
        scopeResolutionTable.addFile(
                Symbol(1, 1, 0),
                listOf(Symbol(1, 0, 0))
        )
        scopeResolutionTable.addScope(
                Symbol(1, 1, 1),
                Symbol(1, 1, 0),
                0
        )
        assertEquals(
                listOf(Symbol(1, 1, 1), Symbol(1, 0, 0)),
                scopeResolutionTable.resolutionEntries(Symbol(1, 1, 1), 0)
        )
    }

    @Test
    fun `parent file not defined`() {
        val scopeResolutionTable = KtScopeResolutionTable()
        assertThrowsMessage<LineException>("resolution entries of file symbol (1, 1, 0) have not been defined") {
            scopeResolutionTable.addScope(
                    Symbol(1, 1, 1),
                    Symbol(1, 1, 0),
                    0
            )
        }
    }

    @Test
    fun `parent declaration not defined`() {
        val scopeResolutionTable = KtScopeResolutionTable()
        assertThrowsMessage<LineException>("parent of scope (1, 1, 1) has not been defined") {
            scopeResolutionTable.addScope(
                    Symbol(1, 1, 2),
                    Symbol(1, 1, 1),
                    0
            )
        }
    }
}