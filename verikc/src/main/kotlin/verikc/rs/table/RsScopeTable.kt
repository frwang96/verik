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
import verikc.base.symbol.SymbolEntry

class RsScopeTable(
    override val symbol: Symbol
): SymbolEntry {

    data class IdentifierEntry(
        val symbol: Symbol,
        val identifier: String
    )

    private val types = ArrayList<IdentifierEntry>()
    private val functions = ArrayList<IdentifierEntry>()
    private val properties = ArrayList<IdentifierEntry>()

    fun addType(type: RsTypeEntry, line: Line) {
        if (types.any { it.identifier == type.identifier }) {
            throw LineException("type ${type.identifier} has already been defined in scope $symbol", line)
        }
        types.add(IdentifierEntry(type.symbol, type.identifier))
    }

    fun addFunction(function: RsFunctionEntry, line: Line) {
        if (functions.any { it.symbol == function.symbol }) {
            throw LineException("function ${function.identifier} has already been defined in scope $symbol", line)
        }
        functions.add(IdentifierEntry(function.symbol, function.identifier))
    }

    fun addProperty(property: RsPropertyEntry, line: Line) {
        if (properties.any { it.identifier == property.identifier }) {
            throw LineException("property ${property.identifier} has already been defined in scope $symbol", line)
        }
        properties.add(IdentifierEntry(property.symbol, property.identifier))
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
