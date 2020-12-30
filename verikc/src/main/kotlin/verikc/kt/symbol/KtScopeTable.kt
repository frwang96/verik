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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry

class KtScopeTable(
    override val symbol: Symbol
): SymbolEntry {

    data class EntryPair(
        val identifier: String,
        val symbol: Symbol
    )

    private val types = ArrayList<EntryPair>()
    private val functions = ArrayList<EntryPair>()
    private val properties = ArrayList<EntryPair>()

    fun addType(type: KtTypeEntry, line: Line) {
        if (types.any { it.identifier == type.identifier }) {
            throw LineException("type ${type.identifier} has already been defined in scope $symbol", line)
        }
        types.add(EntryPair(type.identifier, type.symbol))
    }

    fun addFunction(function: KtFunctionEntry, line: Line) {
        if (functions.any { it.symbol == function.symbol }) {
            throw LineException("function ${function.identifier} has already been defined in scope $symbol", line)
        }
        functions.add(EntryPair(function.identifier, function.symbol))
    }

    fun addProperty(property: KtPropertyEntry, line: Line) {
        if (properties.any { it.identifier == property.identifier }) {
            throw LineException("property ${property.identifier} has already been defined in scope $symbol", line)
        }
        properties.add(EntryPair(property.identifier, property.symbol))
    }

    fun resolveTypeSymbol(identifier: String): Symbol? {
        return types.find { it.identifier == identifier }?.symbol
    }

    fun resolveFunctionSymbol(identifier: String): List<Symbol> {
        return functions
            .filter { it.identifier == identifier }
            .map { it.symbol }
    }

    fun resolvePropertySymbol(identifier: String): Symbol? {
        return properties.find { it.identifier == identifier }?.symbol
    }
}
