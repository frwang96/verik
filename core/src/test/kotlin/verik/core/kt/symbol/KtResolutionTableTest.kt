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
import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol

internal class KtResolutionTableTest  {

    @Test
    fun `scope is file`() {
        val resolutionTable = KtResolutionTable()
        resolutionTable.addFile(
                Symbol(1, 1, 0),
                listOf(KtResolutionEntry(listOf(Symbol(1, 1, 0))))
        )
        assertEquals(
                listOf(KtResolutionEntry(listOf(Symbol(1, 1, 0)))),
                resolutionTable.resolutionEntries(Symbol(1, 1, 0), 0)
        )
    }

    @Test
    fun `scope is declaration`() {
        val resolutionTable = KtResolutionTable()
        resolutionTable.addFile(
                Symbol(1, 1, 0),
                listOf(KtResolutionEntry(listOf(Symbol(1, 1, 0))))
        )
        resolutionTable.addScope(
                Symbol(1, 1, 1),
                Symbol(1, 1, 0),
                0
        )
        assertEquals(
                listOf(
                        KtResolutionEntry(listOf(Symbol(1, 1, 1))),
                        KtResolutionEntry(listOf(Symbol(1, 1, 0)))
                ),
                resolutionTable.resolutionEntries(Symbol(1, 1, 1), 0)
        )
    }

    @Test
    fun `scope not defined`() {
        val resolutionTable = KtResolutionTable()
        assertThrowsMessage<LineException>("resolution entries of scope [[1, 1, 0]] have not been defined") {
            resolutionTable.addScope(
                    Symbol(1, 1, 1),
                    Symbol(1, 1, 0),
                    0
            )
        }
    }
}