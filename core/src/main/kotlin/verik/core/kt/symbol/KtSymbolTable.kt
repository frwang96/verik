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
import verik.core.lang.Lang
import verik.core.lang.LangSymbol.SCOPE_LANG
import java.util.concurrent.ConcurrentHashMap

class KtSymbolTable {

    private val resolutionTable = KtResolutionTable()
    private val typeEntryMap = ConcurrentHashMap<Symbol, KtTypeEntry>()
    private val propertyEntryMap = ConcurrentHashMap<Symbol, KtPropertyEntry>()
    private val scopeTableMap = ConcurrentHashMap<Symbol, KtScopeTable>()

    init {
        addFile(
                SCOPE_LANG,
                listOf(KtResolutionEntry(listOf(SCOPE_LANG)))
        )
        for (type in Lang.types) {
            val parents = ArrayList<Symbol>(listOf(type.symbol))
            var parentSymbol = type.parent
            while (parentSymbol != null) {
                parents.add(parentSymbol)
                val parent = Lang.types.find { it.symbol == parentSymbol }
                        ?: throw IllegalArgumentException("could not resolve type $parentSymbol")
                parentSymbol = parent.parent
            }
            val typeEntry = KtTypeEntryLang(
                    type.symbol,
                    type.identifier,
                    parents
            )
            addTypeEntry(typeEntry, SCOPE_LANG, 0)
        }
        for (property in Lang.properties) {
            val propertyEntry = KtPropertyEntryLang(
                    property.symbol,
                    property.type,
                    property.identifier
            )
            addPropertyEntry(propertyEntry, SCOPE_LANG, 0)
        }
    }

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

    fun addTypeEntry(typeEntry: KtTypeEntry, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addType(typeEntry, line)
        if (typeEntryMap[typeEntry.symbol] != null) {
            throw LineException("type ${typeEntry.identifier} has already been defined", line)
        }
        typeEntryMap[typeEntry.symbol] = typeEntry
        addScope(typeEntry.symbol, scope, line)
    }

    fun addFunctionEntry(functionEntry: KtFunctionEntry, scope: Symbol, line: Int) {
        addScope(functionEntry.function.symbol, scope, line)
    }

    fun addPropertyEntry(property: KtPropertyEntry, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addProperty(property, line)
        if (propertyEntryMap[property.symbol] != null) {
            throw LineException("property ${property.identifier} has already been defined", line)
        }
        propertyEntryMap[property.symbol] = property
    }

    fun resolveType(identifier: String, scope: Symbol, line: Int): KtTypeEntry {
        val resolutionEntries = resolutionTable.resolutionEntries(scope, line)
        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val type = getScopeTable(it, line).resolveType(identifier)
                if (type != null) {
                    return getTypeEntry(type, line)
                }
            }
        }
        throw LineException("could not resolve type $identifier", line)
    }

    fun resolveProperty(identifier: String, scope: Symbol, line: Int): KtPropertyEntry {
        val resolutionEntries = resolutionTable.resolutionEntries(scope, line)
        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val property = getScopeTable(it, line).resolveProperty(identifier)
                if (property != null) {
                    return getPropertyEntry(property, line)
                }
            }
        }
        throw LineException("could not resolve property $identifier", line)
    }

    private fun addScope(scope: Symbol, parent: Symbol, line: Int) {
        resolutionTable.addScope(scope, parent, line)
        if (scopeTableMap[scope] != null) {
            throw LineException("scope table for $scope has already been defined", line)
        }
        scopeTableMap[scope] = KtScopeTable(scope)
    }

    private fun getScopeTable(scope: Symbol, line: Int): KtScopeTable {
        return scopeTableMap[scope]
                ?: throw LineException("scope $scope has not been defined", line)
    }

    private fun getTypeEntry(type: Symbol, line: Int): KtTypeEntry {
        return typeEntryMap[type]
                ?: throw LineException("type $type has not been defined", line)
    }

    private fun getPropertyEntry(property: Symbol, line: Int): KtPropertyEntry {
        return propertyEntryMap[property]
                ?: throw LineException("property $property has not been defined", line)
    }
}
