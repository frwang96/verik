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
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rsx.symbol.RsxResolutionEntry

internal class RsxResolutionTableTest {

    @Test
    fun `scope is file`() {
        val resolutionTable = RsxResolutionTable()
        resolutionTable.addFile(Symbol(2), listOf(RsxResolutionEntry(listOf(Symbol(2)), listOf())))
        assertEquals(
            listOf(RsxResolutionEntry(listOf(Symbol(2)), listOf())),
            resolutionTable.resolutionEntries(Symbol(2), Line(0))
        )
    }

    @Test
    fun `scope is declaration`() {
        val resolutionTable = RsxResolutionTable()
        resolutionTable.addFile(Symbol(2), listOf(RsxResolutionEntry(listOf(Symbol(2)), listOf())))
        resolutionTable.addScope(Symbol(3), Symbol(2), Line(0))
        assertEquals(
            listOf(
                RsxResolutionEntry(listOf(Symbol(3)), listOf()),
                RsxResolutionEntry(listOf(Symbol(2)), listOf())
            ),
            resolutionTable.resolutionEntries(Symbol(3), Line(0))
        )
    }

    @Test
    fun `scope not defined`() {
        val resolutionTable = RsxResolutionTable()
        assertThrowsMessage<LineException>("resolution entries of scope [[2]] have not been defined") {
            resolutionTable.addScope(Symbol(3), Symbol(2), Line(0))
        }
    }
}
