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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry

class RsxScopeTable(
    override val symbol: Symbol
): SymbolEntry {

    data class IdentifierEntry(
        val symbol: Symbol,
        val identifier: String
    )

    private val types = ArrayList<IdentifierEntry>()
    private val functions = ArrayList<IdentifierEntry>()

    fun addType(typeEntry: RsxTypeEntry, line: Line) {
        if (types.any { it.identifier == typeEntry.identifier }) {
            throw LineException("type ${typeEntry.identifier} has already been defined in scope $symbol", line)
        }
        types.add(IdentifierEntry(typeEntry.symbol, typeEntry.identifier))
    }

    fun addFunction(functionEntry: RsxFunctionEntry, line: Line) {
        if (functions.any { it.symbol == functionEntry.symbol }) {
            throw LineException("function ${functionEntry.identifier} has already been defined in scope $symbol", line)
        }
        functions.add(IdentifierEntry(functionEntry.symbol, functionEntry.identifier))
    }

    fun resolveTypeSymbol(identifier: String): Symbol? {
        return types.find { it.identifier == identifier }?.symbol
    }

    fun resolveFunctionSymbol(identifier: String): List<Symbol> {
        return functions
            .filter { it.identifier == identifier }
            .map { it.symbol }
    }
}
