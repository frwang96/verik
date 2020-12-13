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

package verikc.kt.symbol

import verikc.base.SymbolEntryMap
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.*
import verikc.lang.Lang
import verikc.lang.LangSymbol.SCOPE_LANG

data class KtSymbolTableResolveResult(
    val symbol: Symbol,
    val type: Symbol
)

class KtSymbolTable {

    private val resolutionTable = KtResolutionTable()
    private val scopeTableMap = SymbolEntryMap<KtScopeTable>("scope")

    private val typeEntryMap = SymbolEntryMap<KtTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<KtFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<KtOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<KtPropertyEntry>("property")

    init {
        loadLang()
    }

    fun addFile(fileSymbol: Symbol, resolutionEntries: List<KtResolutionEntry>) {
        resolutionTable.addFile(fileSymbol, resolutionEntries)
        scopeTableMap.add(KtScopeTable(fileSymbol), Line(fileSymbol, 0))
    }

    fun addScope(scopeSymbol: Symbol, parentSymbol: Symbol, line: Line) {
        resolutionTable.addScope(scopeSymbol, parentSymbol, line)
        scopeTableMap.add(KtScopeTable(scopeSymbol), line)
    }

    fun addType(type: KtType, scopeSymbol: Symbol) {
        val typeEntry = KtTypeEntryRegular(
            type.symbol,
            type.identifier,
            null,
            type.typeParent.typeIdentifier,
            scopeSymbol
        )
        addTypeEntry(typeEntry, scopeSymbol, type.line)
    }

    fun addFunction(function: KtFunction, scopeSymbol: Symbol) {
        val returnTypeSymbol = function.returnTypeSymbol
            ?: throw LineException("function return type has not been resolved", function.line)
        val argTypeSymbols = function.parameters.map {
            it.typeSymbol ?: throw LineException("function argument ${it.identifier} has not been resolved", function.line)
        }
        val functionEntry = KtFunctionEntry(
            function.symbol,
            function.identifier,
            returnTypeSymbol,
            argTypeSymbols
        )
        addFunctionEntry(functionEntry, scopeSymbol, function.line)
    }

    fun addProperty(type: KtType, scopeSymbol: Symbol) {
        val propertyEntry = KtPropertyEntry(
            type.symbol,
            type.identifier,
            type.symbol
        )
        addPropertyEntry(propertyEntry, scopeSymbol, type.line)
    }

    fun addProperty(property: KtProperty, scopeSymbol: Symbol) {
        val typeSymbol = property.typeSymbol
            ?: throw LineException("property ${property.identifier} has not been resolved", property.line)
        val propertyEntry = KtPropertyEntry(
            property.symbol,
            property.identifier,
            typeSymbol
        )
        addPropertyEntry(propertyEntry, scopeSymbol, property.line)
    }

    fun resolveType(identifier: String, scopeSymbol: Symbol, line: Line): Symbol {
        val resolutionEntries = resolutionTable.resolutionEntries(scopeSymbol, line)
        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopeSymbols.forEach {
                val typeSymbol = scopeTableMap.get(it, line).resolveTypeSymbol(identifier)
                if (typeSymbol != null) {
                    return typeSymbol
                }
            }
        }
        throw LineException("could not resolve type $identifier", line)
    }

    fun resolveFunction(expression: KtExpressionFunction, scopeSymbol: Symbol): KtSymbolTableResolveResult {
        val argTypeSymbols = expression.args.map {
            it.typeSymbol ?: throw LineException("expression has not been resolved", it.line)
        }
        val argsParents = argTypeSymbols.map {
            getParentSymbols(it, expression.line)
        }

        val resolutionEntries = getResolutionEntries(expression.receiver, scopeSymbol, expression.line)

        for (resolutionEntry in resolutionEntries) {
            for (resolutionScope in resolutionEntry.scopeSymbols) {
                val functionEntries = scopeTableMap.get(resolutionScope, expression.line)
                    .resolveFunctionSymbol(expression.identifier)
                    .map { functionEntryMap.get(it, expression.line) }
                    .filter { it.matches(argsParents) }
                if (functionEntries.isNotEmpty()) {
                    val functionEntry = functionEntries.first()
                    return KtSymbolTableResolveResult(functionEntry.symbol, functionEntry.returnTypeSymbol)
                }
            }
        }

        throw LineException("could not resolve function ${expression.identifier}", expression.line)
    }

    fun resolveOperator(expression: KtExpressionOperator): Symbol {
        return operatorEntryMap.get(expression.operatorSymbol, expression.line).resolver(expression)
    }

    fun resolveProperty(expression: KtExpressionProperty, scope: Symbol): KtSymbolTableResolveResult {
        val resolutionEntries = getResolutionEntries(expression.receiver, scope, expression.line)

        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopeSymbols.forEach {
                val propertySymbol = scopeTableMap.get(it, expression.line)
                    .resolvePropertySymbol(expression.identifier)
                if (propertySymbol != null) {
                    return KtSymbolTableResolveResult(
                        propertySymbol,
                        propertyEntryMap.get(propertySymbol, expression.line).typeSymbol
                    )
                }
            }
        }

        throw LineException("could not resolve property ${expression.identifier}", expression.line)
    }

    private fun loadLang() {
        addFile(
            SCOPE_LANG,
            listOf(KtResolutionEntry(listOf(SCOPE_LANG)))
        )
        for (type in Lang.types) {
            val typeEntry = KtTypeEntryLang(
                type.symbol,
                type.identifier,
                null,
                type.parentSymbol
            )
            addScope(type.symbol, SCOPE_LANG, Line(0))
            addTypeEntry(typeEntry, SCOPE_LANG, Line(0))
        }
        for (function in Lang.functions) {
            val functionEntry = KtFunctionEntry(
                function.symbol,
                function.identifier,
                function.returnTypeSymbol,
                function.argTypeSymbols
            )
            val scope = function.receiverTypeSymbol ?: SCOPE_LANG
            addFunctionEntry(functionEntry, scope, Line(0))
        }
        for (operator in Lang.operators) {
            val operatorEntry = KtOperatorEntry(
                operator.symbol,
                operator.identifier,
                operator.resolver
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
        for (property in Lang.properties) {
            val propertyEntry = KtPropertyEntry(
                property.symbol,
                property.identifier,
                property.typeSymbol
            )
            addPropertyEntry(propertyEntry, SCOPE_LANG, Line(0))
        }
    }

    private fun addTypeEntry(typeEntry: KtTypeEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addType(typeEntry, line)
        typeEntryMap.add(typeEntry, line)
    }

    private fun addFunctionEntry(functionEntry: KtFunctionEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addFunction(functionEntry, line)
        functionEntryMap.add(functionEntry, line)
    }

    private fun addPropertyEntry(propertyEntry: KtPropertyEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addProperty(propertyEntry, line)
        propertyEntryMap.add(propertyEntry, line)
    }

    private fun getResolutionEntries(receiver: KtExpression?, scopeSymbol: Symbol, line: Line): List<KtResolutionEntry> {
        return if (receiver != null) {
            val receiverType = receiver.typeSymbol
                ?: throw LineException("expression receiver has not been resolved", line)
            getParentSymbols(receiverType, line)
                .map { KtResolutionEntry(listOf(it)) }
        } else {
            resolutionTable.resolutionEntries(scopeSymbol, line)
        }
    }

    private fun getParentSymbols(typeSymbol: Symbol, line: Line): List<Symbol> {
        val typeEntry = typeEntryMap.get(typeSymbol, line)
        return typeEntry.parentSymbols ?: when (typeEntry) {
            is KtTypeEntryRegular -> {
                val parent = resolveType(typeEntry.parentIdentifier, typeEntry.scope, line)
                listOf(typeSymbol) + getParentSymbols(parent, line)
            }
            is KtTypeEntryLang -> {
                if (typeEntry.parent != null) {
                    listOf(typeSymbol) + getParentSymbols(typeEntry.parent, line)
                } else {
                    listOf(typeSymbol)
                }
            }
        }
    }
}
