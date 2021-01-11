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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class RsResolutionTable {

    private val resolutionEntriesMap = ConcurrentHashMap<Symbol, List<RsResolutionEntry>>()

    fun addFile(fileSymbol: Symbol, resolutionEntries: List<RsResolutionEntry>) {
        if (resolutionEntriesMap[fileSymbol] != null) {
            throw LineException("resolution entries of file $fileSymbol have already been defined", Line(fileSymbol, 0))
        }
        resolutionEntriesMap[fileSymbol] = resolutionEntries
    }

    fun addScope(scopeSymbol: Symbol, parentSymbol: Symbol, line: Line) {
        if (resolutionEntriesMap[scopeSymbol] != null) {
            throw LineException("resolution entries of scope $scopeSymbol have already been defined", line)
        }
        val parentResolutionEntries = resolutionEntries(parentSymbol, line)
        val resolutionEntry = RsResolutionEntry(listOf(scopeSymbol), listOf())
        resolutionEntriesMap[scopeSymbol] = listOf(resolutionEntry) + parentResolutionEntries
    }

    fun resolutionEntries(scopeSymbol: Symbol, line: Line): List<RsResolutionEntry> {
        return resolutionEntriesMap[scopeSymbol]
            ?: throw LineException("resolution entries of scope $scopeSymbol have not been defined", line)
    }
}
