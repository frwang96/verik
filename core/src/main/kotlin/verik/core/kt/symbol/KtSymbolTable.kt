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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtDeclarationProperty
import java.util.concurrent.ConcurrentHashMap

class KtSymbolTable {

    private val resolutionTable = KtResolutionTable()
    private val typeMap = ConcurrentHashMap<Symbol, KtTypeEntry>()
    private val propertyMap = ConcurrentHashMap<Symbol, KtDeclarationProperty>()
    private val scopeTableMap = ConcurrentHashMap<Symbol, KtScopeTable>()

    fun addFile(file: Symbol, resolutionEntries: List<KtResolutionEntry>) {
        if (!file.isFileSymbol()) {
            throw LineException("file expected but got $file", 0)
        }
        resolutionTable.addFile(file, resolutionEntries)
        if (scopeTableMap[file] != null) {
            throw IllegalArgumentException("scope table for $file has already been defined")
        }
        scopeTableMap[file] = KtScopeTable(file)
    }

    fun addScope(scope: Symbol, parent: Symbol, line: Int) {
        resolutionTable.addScope(scope, parent, line)
        if (scopeTableMap[scope] != null) {
            throw LineException("scope table for $scope has already been defined", line)
        }
        scopeTableMap[scope] = KtScopeTable(scope)
    }

    fun addType(typeEntry: KtTypeEntry, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addType(typeEntry, line)
        if (typeMap[typeEntry.symbol] != null) {
            throw LineException("type entry ${typeEntry.identifier} has already been defined", line)
        }
        typeMap[typeEntry.symbol] = typeEntry
    }

    fun addProperty(property: KtDeclarationProperty, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addProperty(property)
        if (propertyMap[property.symbol] != null) {
            throw LineException("property entry ${property.identifier} has already been defined", line)
        }
        propertyMap[property.symbol] = property
    }

    fun resolveType(identifier: String, scope: Symbol, line: Int): Symbol {
        val resolutionEntries = resolutionTable.resolutionEntries(scope, line)
        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val type = getScopeTable(it, line).resolveType(identifier)
                if (type != null) {
                    return type
                }
            }
        }
        throw LineException("could not resolve type $identifier", line)
    }

    fun resolveProperty(identifier: String, scope: Symbol, line: Int): KtDeclarationProperty? {
        val resolutionEntries = resolutionTable.resolutionEntries(scope, line)
        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val property = getScopeTable(it, line).resolveProperty(identifier)
                if (property != null) {
                    return getProperty(property, line)
                }
            }
        }
        return null
    }

    private fun getScopeTable(scope: Symbol, line: Int): KtScopeTable {
        return scopeTableMap[scope]
                ?: throw LineException("scope $scope has not been defined", line)
    }

    private fun getProperty(property: Symbol, line: Int): KtDeclarationProperty {
        return propertyMap[property]
                ?: throw LineException("property $property has not been defined", line)
    }
}
