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
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.LangDeclaration
import verikc.lang.LangSymbol.SCOPE_LANG
import verikc.rsx.ast.RsxFile
import verikc.rsx.symbol.RsxResolutionEntry

class RsxSymbolTable {

    private val resolutionTable = RsxResolutionTable()
    private val scopeTableMap = SymbolEntryMap<RsxScopeTable>("scope")

    private val typeEntryMap = SymbolEntryMap<RsxTypeEntry>("type")

    init {
        resolutionTable.addFile(SCOPE_LANG, listOf(RsxResolutionEntry(listOf(SCOPE_LANG), listOf())))
        scopeTableMap.add(RsxScopeTable(SCOPE_LANG), Line(0))
        for (type in LangDeclaration.types) {
            val typeEntry = RsxTypeEntry(type.symbol, type.identifier)
            addTypeEntry(typeEntry, SCOPE_LANG, Line(0))
        }
    }

    fun addFile(file: RsxFile) {
        val fileSymbol = file.config.symbol
        val resolutionEntries = file.resolutionEntries
            ?: throw LineException("resolution entries of file $fileSymbol have not been set", Line(fileSymbol, 0))
        resolutionTable.addFile(fileSymbol, resolutionEntries)
        scopeTableMap.add(RsxScopeTable(fileSymbol), Line(fileSymbol, 0))
    }

    fun resolveTypeSymbol(identifier: String, scopeSymbol: Symbol, line: Line): Symbol {
        val resolutionEntries = resolutionTable.resolutionEntries(scopeSymbol, line)
        for (resolutionEntry in resolutionEntries) {
            val typeSymbols = ArrayList<Symbol>()
            resolutionEntry.scopeSymbols.forEach {
                val typeSymbol = scopeTableMap.get(it, line).resolveTypeSymbol(identifier)
                if (typeSymbol != null) {
                    typeSymbols.add(typeSymbol)
                }
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in typeEntryMap) {
                    val typeEntry = typeEntryMap.get(it, line)
                    if (typeEntry.identifier == identifier) {
                        typeSymbols.add(it)
                    }
                }
            }
            if (typeSymbols.isNotEmpty()) {
                if (typeSymbols.size > 1) throw LineException("could not resolve type ambiguity for $identifier", line)
                return typeSymbols[0]
            }
        }
        throw LineException("could not resolve type $identifier", line)
    }

    private fun addTypeEntry(typeEntry: RsxTypeEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addType(typeEntry, line)
        typeEntryMap.add(typeEntry, line)
    }
}
