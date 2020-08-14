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

package verik.core.kt.resolve

import verik.core.kt.KtDeclarationProperty
import verik.core.kt.KtDeclarationType
import verik.core.main.LineException
import verik.core.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class KtSymbolTable {

    private val scopeResolutionTable = KtScopeResolutionTable()
    private val scopeTableMap = ConcurrentHashMap<Symbol, KtScopeTable>()
    private val propertyMap = ConcurrentHashMap<Symbol, KtDeclarationProperty>()

    fun addPkg(pkg: Symbol) {
        if (!pkg.isPkgSymbol()) {
            throw IllegalArgumentException("package symbol expected but got $pkg")
        }
        if (scopeTableMap[pkg] != null) {
            throw IllegalArgumentException("scope table for packge symbol $pkg has already been defined")
        }
        scopeTableMap[pkg] = KtScopeTable()
    }

    fun addFile(file: Symbol) {
        if (!file.isFileSymbol()) {
            throw LineException("file symbol expected but got $file", 0)
        }
        scopeResolutionTable.addFile(file, listOf(file.toPkgSymbol()))
    }

    fun addType(type: KtDeclarationType, file: Symbol, line: Int) {
        scopeResolutionTable.addScope(type.symbol, file, line)
        if (scopeTableMap[type.symbol] != null) {
            throw LineException("scope table for file symbol $file has already been defined", line)
        }
        scopeTableMap[type.symbol] = KtScopeTable()
    }

    fun addProperty(property: KtDeclarationProperty, parent: Symbol, line: Int) {
        when {
            parent.isPkgSymbol() -> {
                throw LineException("parent of property cannot be package symbol $parent", line)
            }
            parent.isFileSymbol() -> {
                getScopeTable(parent.toPkgSymbol(), line).addProperty(property, line)
            }
            parent.isDeclarationSymbol() -> {
                getScopeTable(parent, line).addProperty(property, line)
            }
        }
        propertyMap[property.symbol] = property
    }

    fun matchProperty(parent: Symbol, identifier: String, line: Int): KtDeclarationProperty? {
        val resolutionEntries = scopeResolutionTable.resolutionEntries(parent, line)
        for (resolutionEntry in resolutionEntries) {
            val property = getScopeTable(resolutionEntry, line).matchProperty(identifier)
            if (property != null) {
                return getProperty(property, line)
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
                ?: throw LineException("property symbol $property has not been defined", line)
    }
}
