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

package verikc.kt.symbol

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol

internal class KtResolutionTableTest {

    @Test
    fun `scope is file`() {
        val resolutionTable = KtResolutionTable()
        resolutionTable.addFile(Symbol(2), listOf(KtResolutionEntry(listOf(Symbol(2)))))
        assertEquals(
            listOf(KtResolutionEntry(listOf(Symbol(2)))),
            resolutionTable.resolutionEntries(Symbol(2), Line(0))
        )
    }

    @Test
    fun `scope is declaration`() {
        val resolutionTable = KtResolutionTable()
        resolutionTable.addFile(Symbol(2), listOf(KtResolutionEntry(listOf(Symbol(2)))))
        resolutionTable.addScope(Symbol(3), Symbol(2), Line(0))
        assertEquals(
            listOf(
                KtResolutionEntry(listOf(Symbol(3))),
                KtResolutionEntry(listOf(Symbol(2)))
            ),
            resolutionTable.resolutionEntries(Symbol(3), Line(0))
        )
    }

    @Test
    fun `scope not defined`() {
        val resolutionTable = KtResolutionTable()
        assertThrowsMessage<LineException>("resolution entries of scope [[2]] have not been defined") {
            resolutionTable.addScope(Symbol(3), Symbol(2), Line(0))
        }
    }
}
