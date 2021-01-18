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

package verikc.base.symbol

import verikc.base.ast.Line
import verikc.base.ast.LineException
import java.util.concurrent.ConcurrentHashMap

class SymbolEntryMap<T: SymbolEntry>(
        private val entryString: String
) {

    private val entryMap = ConcurrentHashMap<Symbol, T>()

    fun add(entry: T, line: Line) {
        if (entryMap[entry.symbol] != null) {
            throw LineException("$entryString ${entry.symbol} has already been defined", line)
        }
        entryMap[entry.symbol] = entry
    }

    fun get(symbol: Symbol, line: Line): T {
        return entryMap[symbol]
                ?: throw LineException("$entryString $symbol has not been defined", line)
    }

    fun getOrNull(symbol: Symbol): T? {
        return entryMap[symbol]
    }

    operator fun contains(symbol: Symbol): Boolean {
        return entryMap.containsKey(symbol)
    }
}
