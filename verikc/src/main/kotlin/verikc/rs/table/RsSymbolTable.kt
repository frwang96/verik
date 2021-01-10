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
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.LangDeclaration
import verikc.lang.LangSymbol.SCOPE_LANG
import verikc.rs.ast.*
import verikc.rs.resolve.RsFunctionOverloadResolver

data class RsSymbolTableResolveResult(
    val symbol: Symbol,
    val typeSymbol: Symbol
)

class RsSymbolTable {

    private val resolutionTable = RsResolutionTable()
    private val scopeTableMap = SymbolEntryMap<RsScopeTable>("scope")

    private val typeEntryMap = SymbolEntryMap<RsTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<RsFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<RsOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<RsPropertyEntry>("property")

    init {
        loadLang()
    }

    fun addFile(file: RsFile) {
        val fileSymbol = file.config.symbol
        val resolutionEntries = file.resolutionEntries
            ?: throw LineException("resolution entries of file $fileSymbol have not been set", Line(fileSymbol, 0))
        resolutionTable.addFile(fileSymbol, resolutionEntries)
        scopeTableMap.add(RsScopeTable(fileSymbol), Line(fileSymbol, 0))
    }

    fun addScope(scopeSymbol: Symbol, parentSymbol: Symbol, line: Line) {
        resolutionTable.addScope(scopeSymbol, parentSymbol, line)
        scopeTableMap.add(RsScopeTable(scopeSymbol), line)
    }

    fun addType(type: RsType, scopeSymbol: Symbol) {
        val typeEntry = RsTypeEntryRegular(
            type.symbol,
            type.identifier,
            null,
            type.typeParent.typeIdentifier,
            scopeSymbol
        )
        addTypeEntry(typeEntry, scopeSymbol, type.line)
    }

    fun addFunction(function: RsFunction, scopeSymbol: Symbol) {
        val returnTypeSymbol = function.returnTypeSymbol
            ?: throw LineException("function return type has not been resolved", function.line)
        val argTypeSymbols = function.parameterProperties.map {
            it.typeSymbol ?: throw LineException("function argument ${it.identifier} has not been resolved", function.line)
        }
        val functionEntry = RsFunctionEntry(
            function.symbol,
            function.identifier,
            argTypeSymbols,
            false,
            returnTypeSymbol
        )
        addFunctionEntry(functionEntry, scopeSymbol, function.line)
    }

    fun addProperty(type: RsType, scopeSymbol: Symbol) {
        val propertyEntry = RsPropertyEntry(
            type.symbol,
            type.identifier,
            type.symbol
        )
        addPropertyEntry(propertyEntry, scopeSymbol, type.line)
    }

    fun addProperty(property: RsProperty, scopeSymbol: Symbol) {
        val typeSymbol = property.typeSymbol
            ?: throw LineException("property ${property.identifier} has not been resolved", property.line)
        val propertyEntry = RsPropertyEntry(
            property.symbol,
            property.identifier,
            typeSymbol
        )
        addPropertyEntry(propertyEntry, scopeSymbol, property.line)
    }

    fun resolveType(identifier: String, scopeSymbol: Symbol, line: Line): Symbol {
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

    fun resolveFunction(expression: RsExpressionFunction, scopeSymbol: Symbol): RsSymbolTableResolveResult {
        val argTypeSymbols = expression.args.map {
            it.getTypeSymbolNotNull()
        }
        val argsParentTypeSymbols = argTypeSymbols.map {
            getParentTypeSymbols(it, expression.line)
        }

        val resolutionEntries = getResolutionEntries(expression.receiver, scopeSymbol, expression.line)

        for (resolutionEntry in resolutionEntries) {
            val functionEntries = ArrayList<RsFunctionEntry>()
            for (resolutionScopeSymbol in resolutionEntry.scopeSymbols) {
                val newFunctionEntries = scopeTableMap.get(resolutionScopeSymbol, expression.line)
                    .resolveFunctionSymbol(expression.identifier)
                    .map { functionEntryMap.get(it, expression.line) }
                    .filter { RsFunctionOverloadResolver.matches(argsParentTypeSymbols, it) }
                functionEntries.addAll(newFunctionEntries)
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in functionEntryMap) {
                    val functionEntry = functionEntryMap.get(it, expression.line)
                    if (functionEntry.identifier == expression.identifier
                        && RsFunctionOverloadResolver.matches(argsParentTypeSymbols, functionEntry)) {
                        functionEntries.add(functionEntry)
                    }
                }
            }
            if (functionEntries.isNotEmpty()) {
                val functionsArgsParentTypeSymbols = functionEntries.map { functionEntry ->
                    functionEntry.argTypeSymbols.map { argTypeSymbol ->
                        getParentTypeSymbols(argTypeSymbol, expression.line)
                    }
                }
                val functionEntry = RsFunctionOverloadResolver.dominatingFunctionEntry(
                    functionEntries,
                    functionsArgsParentTypeSymbols,
                    expression.line
                )
                return RsSymbolTableResolveResult(functionEntry.symbol, functionEntry.returnTypeSymbol)
            }
        }

        throw LineException("could not resolve function ${expression.identifier}", expression.line)
    }

    fun resolveOperator(expression: RsExpressionOperator): Symbol {
        return operatorEntryMap
            .get(expression.operatorSymbol, expression.line)
            .resolver(RsOperatorResolverRequest(expression, this))
    }

    fun resolveProperty(expression: RsExpressionProperty, scope: Symbol): RsSymbolTableResolveResult {
        val resolutionEntries = getResolutionEntries(expression.receiver, scope, expression.line)

        for (resolutionEntry in resolutionEntries) {
            val propertySymbols = ArrayList<Symbol>()
            resolutionEntry.scopeSymbols.forEach {
                val propertySymbol = scopeTableMap
                    .get(it, expression.line)
                    .resolvePropertySymbol(expression.identifier)
                if (propertySymbol != null) {
                    propertySymbols.add(propertySymbol)
                }
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in propertyEntryMap) {
                    val propertyEntry = propertyEntryMap.get(it, expression.line)
                    if (propertyEntry.identifier == expression.identifier) {
                        propertySymbols.add(it)
                    }
                }
            }
            if (propertySymbols.isNotEmpty()) {
                if (propertySymbols.size > 1)
                    throw LineException(
                        "could not resolve property ambiguity for ${expression.identifier}",
                        expression.line
                    )
                return RsSymbolTableResolveResult(
                    propertySymbols[0],
                    propertyEntryMap.get(propertySymbols[0], expression.line).typeSymbol
                )
            }
        }

        throw LineException("could not resolve property ${expression.identifier}", expression.line)
    }

    fun getParentTypeSymbols(typeSymbol: Symbol, line: Line): List<Symbol> {
        val typeEntry = typeEntryMap.get(typeSymbol, line)
        if (typeEntry.parentTypeSymbols == null) {
            typeEntry.parentTypeSymbols = when (typeEntry) {
                is RsTypeEntryRegular -> {
                    val parent = resolveType(typeEntry.parentIdentifier, typeEntry.scope, line)
                    listOf(typeSymbol) + getParentTypeSymbols(parent, line)
                }
                is RsTypeEntryLang -> {
                    if (typeEntry.parent != null) {
                        listOf(typeSymbol) + getParentTypeSymbols(typeEntry.parent, line)
                    } else {
                        listOf(typeSymbol)
                    }
                }
            }
        }
        return typeEntry.parentTypeSymbols!!
    }

    private fun loadLang() {
        resolutionTable.addFile(SCOPE_LANG, listOf(RsResolutionEntry(listOf(SCOPE_LANG), listOf())))
        scopeTableMap.add(RsScopeTable(SCOPE_LANG), Line(SCOPE_LANG, 0))
        for (type in LangDeclaration.types) {
            val typeEntry = RsTypeEntryLang(
                type.symbol,
                type.identifier,
                null,
                type.parentTypeSymbol
            )
            addScope(type.symbol, SCOPE_LANG, Line(0))
            addTypeEntry(typeEntry, SCOPE_LANG, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = RsFunctionEntry(
                function.symbol,
                function.identifier,
                function.argTypeSymbols,
                function.isVararg,
                function.returnTypeSymbol
            )
            val scope = function.receiverTypeSymbol ?: SCOPE_LANG
            addFunctionEntry(functionEntry, scope, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = RsOperatorEntry(
                operator.symbol,
                operator.resolver
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    private fun addTypeEntry(typeEntry: RsTypeEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addType(typeEntry, line)
        typeEntryMap.add(typeEntry, line)
    }

    private fun addFunctionEntry(functionEntry: RsFunctionEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addFunction(functionEntry, line)
        functionEntryMap.add(functionEntry, line)
    }

    private fun addPropertyEntry(propertyEntry: RsPropertyEntry, scopeSymbol: Symbol, line: Line) {
        scopeTableMap.get(scopeSymbol, line).addProperty(propertyEntry, line)
        propertyEntryMap.add(propertyEntry, line)
    }

    private fun getResolutionEntries(
        receiver: RsExpression?,
        scopeSymbol: Symbol,
        line: Line
    ): List<RsResolutionEntry> {
        return if (receiver != null) {
            getParentTypeSymbols(receiver.getTypeSymbolNotNull(), line)
                .map { RsResolutionEntry(listOf(it), listOf()) }
        } else {
            resolutionTable.resolutionEntries(scopeSymbol, line)
        }
    }
}
