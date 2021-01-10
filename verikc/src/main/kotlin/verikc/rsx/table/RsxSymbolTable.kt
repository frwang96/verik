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
import verikc.rsx.ast.RsxExpression
import verikc.rsx.ast.RsxExpressionFunction
import verikc.rsx.ast.RsxFile
import verikc.rsx.ast.RsxType
import verikc.rsx.resolve.RsxResolverFunction
import verikc.rsx.resolve.RsxResolverSymbolResult

class RsxSymbolTable {

    private val resolutionTable = RsxResolutionTable()
    private val scopeTableMap = SymbolEntryMap<RsxScopeTable>("scope")

    private val typeEntryMap = SymbolEntryMap<RsxTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<RsxFunctionEntry>("function")

    init {
        resolutionTable.addFile(SCOPE_LANG, listOf(RsxResolutionEntry(listOf(SCOPE_LANG), listOf())))
        scopeTableMap.add(RsxScopeTable(SCOPE_LANG), Line(0))
        for (type in LangDeclaration.types) {
            val typeEntry = RsxTypeEntryLang(
                type.symbol,
                type.identifier,
                null,
                type.parentTypeSymbol
            )
            addScope(type.symbol, SCOPE_LANG, Line(0))
            addTypeEntry(typeEntry, SCOPE_LANG, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = RsxFunctionEntry(
                function.symbol,
                function.identifier,
                function.argTypeSymbols,
                function.argExpressionClasses,
                function.isVararg,
                function.resolver,
                function.returnExpressionClass
            )
            val scope = function.receiverTypeSymbol ?: SCOPE_LANG
            addFunctionEntry(functionEntry, scope, Line(0))
        }
    }

    fun addFile(file: RsxFile) {
        val fileSymbol = file.config.symbol
        val resolutionEntries = file.resolutionEntries
            ?: throw LineException("resolution entries of file $fileSymbol have not been set", Line(fileSymbol, 0))
        resolutionTable.addFile(fileSymbol, resolutionEntries)
        scopeTableMap.add(RsxScopeTable(fileSymbol), Line(fileSymbol, 0))
    }

    fun addScope(scopeSymbol: Symbol, parentScopeSymbol: Symbol, line: Line) {
        resolutionTable.addScope(scopeSymbol, parentScopeSymbol, line)
        scopeTableMap.add(RsxScopeTable(scopeSymbol), line)
    }

    fun addType(type: RsxType, scopeSymbol: Symbol) {
        val typeEntry = RsxTypeEntryRegular(
            type.symbol,
            type.identifier,
            null,
            type.typeParent.typeIdentifier,
            scopeSymbol
        )
        addTypeEntry(typeEntry, scopeSymbol, type.line)
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

    fun resolveFunction(expression: RsxExpressionFunction): RsxResolverSymbolResult {
        val argTypeSymbols = expression.args.map {
            it.getTypeGenerifiedNotNull().typeSymbol
        }
        val argsParentTypeSymbols = argTypeSymbols.map {
            getParentTypeSymbols(it, expression.line)
        }

        for (resolutionEntry in getResolutionEntries(expression.receiver, SCOPE_LANG, expression.line)) {
            val functionEntries = ArrayList<RsxFunctionEntry>()
            for (resolutionScopeSymbol in resolutionEntry.scopeSymbols) {
                val newFunctionEntries = scopeTableMap
                    .get(resolutionScopeSymbol, expression.line)
                    .resolveFunctionSymbol(expression.identifier)
                    .map { functionEntryMap.get(it, expression.line) }
                    .filter { RsxResolverFunction.matches(argsParentTypeSymbols, it) }
                functionEntries.addAll(newFunctionEntries)
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in functionEntryMap) {
                    val functionEntry = functionEntryMap.get(it, expression.line)
                    if (functionEntry.identifier == expression.identifier
                        && RsxResolverFunction.matches(argsParentTypeSymbols, functionEntry)) {
                        functionEntries.add(functionEntry)
                    }
                }
            }
            if (functionEntries.isNotEmpty()) {
                val functionEntry = RsxResolverFunction.dominatingEntry(functionEntries, this, expression.line)
                return RsxResolverFunction.resolve(expression, functionEntry)
            }
        }

        throw LineException("could not resolve function ${expression.identifier}", expression.line)
    }

    fun getParentTypeSymbols(typeSymbol: Symbol, line: Line): List<Symbol> {
        val typeEntry = typeEntryMap.get(typeSymbol, line)
        if (typeEntry.parentTypeSymbols == null) {
            typeEntry.parentTypeSymbols = when (typeEntry) {
                is RsxTypeEntryLang -> {
                    if (typeEntry.parentTypeSymbol != null) {
                        listOf(typeSymbol) + getParentTypeSymbols(typeEntry.parentTypeSymbol, line)
                    } else {
                        listOf(typeSymbol)
                    }
                }
                is RsxTypeEntryRegular ->{
                    val parent = resolveTypeSymbol(typeEntry.parentIdentifier, typeEntry.scope, line)
                    listOf(typeSymbol) + getParentTypeSymbols(parent, line)
                }
            }
        }
        return typeEntry.parentTypeSymbols!!
    }

    private fun addTypeEntry(typeEntry: RsxTypeEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addType(typeEntry, line)
        typeEntryMap.add(typeEntry, line)
    }

    private fun addFunctionEntry(functionEntry: RsxFunctionEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addFunction(functionEntry, line)
        functionEntryMap.add(functionEntry, line)
    }

    private fun getResolutionEntries(
        receiver: RsxExpression?,
        scopeSymbol: Symbol,
        line: Line
    ): List<RsxResolutionEntry> {
        return if (receiver != null) {
            getParentTypeSymbols(receiver.getTypeGenerifiedNotNull().typeSymbol, line)
                .map { RsxResolutionEntry(listOf(it), listOf()) }
        } else {
            resolutionTable.resolutionEntries(scopeSymbol, line)
        }
    }
}
