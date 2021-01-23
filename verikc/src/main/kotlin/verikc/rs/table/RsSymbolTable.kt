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

import verikc.base.ast.ExpressionClass
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.LangDeclaration
import verikc.lang.LangSymbol.SCOPE_LANG
import verikc.rs.ast.*
import verikc.rs.resolve.RsEvaluateResult
import verikc.rs.resolve.RsResolverFunctionUtil
import verikc.rs.resolve.RsResolverResult

class RsSymbolTable {

    private val resolutionTable = RsResolutionTable()
    private val scopeTableMap = SymbolEntryMap<RsScopeTable>("scope")

    private val typeEntryMap = SymbolEntryMap<RsTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<RsFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<RsOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<RsPropertyEntry>("property")

    init {
        resolutionTable.addFile(SCOPE_LANG, listOf(RsResolutionEntry(listOf(SCOPE_LANG), listOf())))
        scopeTableMap.add(RsScopeTable(SCOPE_LANG), Line(0))
        for (type in LangDeclaration.types) {
            val typeEntry = RsTypeEntryLang(
                type.symbol,
                type.identifier,
                type.hasTypeParameters,
                null,
                type.parentTypeSymbol
            )
            addScope(type.symbol, SCOPE_LANG, Line(0))
            addTypeEntry(typeEntry, SCOPE_LANG, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = RsFunctionEntryLang(
                function.symbol,
                function.identifier,
                function.argTypeSymbols,
                function.argExpressionClasses,
                function.isVararg,
                function.returnExpressionClass,
                function.resolver
            )
            val scope = function.receiverTypeSymbol ?: SCOPE_LANG
            addFunctionEntry(functionEntry, scope, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = RsOperatorEntry(
                operator.symbol,
                operator.resolver,
                operator.returnExpressionClass
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addFile(file: RsFile) {
        val fileSymbol = file.config.symbol
        val resolutionEntries = file.resolutionEntries
            ?: throw LineException("resolution entries of file $fileSymbol have not been set", Line(fileSymbol, 0))
        resolutionTable.addFile(fileSymbol, resolutionEntries)
        scopeTableMap.add(RsScopeTable(fileSymbol), Line(fileSymbol, 0))
    }

    fun addScope(scopeSymbol: Symbol, parentScopeSymbol: Symbol, line: Line) {
        resolutionTable.addScope(scopeSymbol, parentScopeSymbol, line)
        scopeTableMap.add(RsScopeTable(scopeSymbol), line)
    }

    fun addType(type: RsType, scopeSymbol: Symbol) {
        val typeEntry = RsTypeEntryRegular(
            type.symbol,
            type.identifier,
            false,
            null,
            type.typeParent.typeIdentifier,
            scopeSymbol
        )
        addTypeEntry(typeEntry, scopeSymbol, type.line)
    }

    fun addFunction(function: RsFunction, expressionClass: ExpressionClass, scopeSymbol: Symbol) {
        val argTypeGenerified = function.parameterProperties.map {
            it.getTypeGenerifiedNotNull()
        }
        val functionEntry = RsFunctionEntryRegular(
            function.symbol,
            function.identifier,
            argTypeGenerified.map { it.typeSymbol },
            List(argTypeGenerified.size) { VALUE },
            false,
            expressionClass,
            argTypeGenerified,
            function.getReturnTypeGenerifiedNotNull()
        )
        addFunctionEntry(functionEntry, scopeSymbol, function.line)
    }

    fun addProperty(property: RsProperty, scopeSymbol: Symbol) {
        val propertyEntry = RsPropertyEntry(
            property.symbol,
            property.identifier,
            property.typeGenerified,
            property.evaluateResult
        )
        addPropertyEntry(propertyEntry, scopeSymbol, property.line)
    }

    fun setProperty(property: RsProperty) {
        val propertyEntry = propertyEntryMap.get(property.symbol, property.line)
        propertyEntry.typeGenerified = property.getTypeGenerifiedNotNull()
        if (property.mutabilityType == MutabilityType.VAL) propertyEntry.evaluateResult = property.evaluateResult
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

    fun resolveFunction(expression: RsExpressionFunction, scopeSymbol: Symbol): RsResolverResult {
        val argTypeSymbols = expression.args.map {
            it.getTypeGenerifiedNotNull().typeSymbol
        }
        val argsParentTypeSymbols = argTypeSymbols.map {
            getParentTypeSymbols(it, expression.line)
        }

        for (resolutionEntry in getResolutionEntries(expression.receiver, scopeSymbol, expression.line)) {
            val functionEntries = ArrayList<RsFunctionEntry>()
            for (resolutionScopeSymbol in resolutionEntry.scopeSymbols) {
                val newFunctionEntries = scopeTableMap
                    .get(resolutionScopeSymbol, expression.line)
                    .resolveFunctionSymbol(expression.identifier)
                    .map { functionEntryMap.get(it, expression.line) }
                    .filter { RsResolverFunctionUtil.matches(argsParentTypeSymbols, it) }
                functionEntries.addAll(newFunctionEntries)
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in functionEntryMap) {
                    val functionEntry = functionEntryMap.get(it, expression.line)
                    if (functionEntry.identifier == expression.identifier
                        && RsResolverFunctionUtil.matches(argsParentTypeSymbols, functionEntry)) {
                        functionEntries.add(functionEntry)
                    }
                }
            }
            if (functionEntries.isNotEmpty()) {
                val functionEntry = RsResolverFunctionUtil.dominatingEntry(functionEntries, this, expression.line)
                RsResolverFunctionUtil.validate(expression, functionEntry)

                val typeGenerified = when (functionEntry) {
                    is RsFunctionEntryLang -> {
                        functionEntry.resolver(RsFunctionResolverRequest(expression, this))
                            ?: throw LineException(
                                "unable to resolve function ${functionEntry.symbol}",
                                expression.line
                            )
                    }
                    is RsFunctionEntryRegular -> {
                        functionEntry.returnTypeGenerified
                    }
                }
                return RsResolverResult(functionEntry.symbol, typeGenerified, functionEntry.returnExpressionClass)
            }
        }

        throw LineException("could not resolve function ${expression.identifier}", expression.line)
    }

    fun resolveOperator(expression: RsExpressionOperator): RsResolverResult {
        val operatorEntry = operatorEntryMap.get(expression.operatorSymbol, expression.line)
        val typeGenerified = operatorEntry.resolver(RsOperatorResolverRequest(expression, this))
            ?: throw LineException("unable to resolve operator ${operatorEntry.symbol}", expression.line)
        return RsResolverResult(
            operatorEntry.symbol,
            typeGenerified,
            operatorEntry.returnExpressionClass
        )
    }

    fun resolveProperty(expression: RsExpressionProperty, scopeSymbol: Symbol): RsResolverResult {
        for (resolutionEntry in getResolutionEntries(expression.receiver, scopeSymbol, expression.line)) {
            val propertyEntries = ArrayList<RsPropertyEntry>()
            resolutionEntry.scopeSymbols.forEach {
                val propertySymbol = scopeTableMap
                    .get(it, expression.line)
                    .resolvePropertySymbol(expression.identifier)
                if (propertySymbol != null) {
                    propertyEntries.add(propertyEntryMap.get(propertySymbol, expression.line))
                }
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in propertyEntryMap) {
                    val propertyEntry = propertyEntryMap.get(it, expression.line)
                    if (propertyEntry.identifier == expression.identifier) {
                        propertyEntries.add(propertyEntry)
                    }
                }
            }

            if (propertyEntries.isNotEmpty()) {
                if (propertyEntries.size > 1)
                    throw LineException(
                        "could not resolve property ambiguity for ${expression.identifier}",
                        expression.line
                    )

                val typeGenerified = propertyEntries[0].typeGenerified
                    ?: throw RsPropertyResolveException(propertyEntries[0].symbol, expression.line)
                return RsResolverResult(propertyEntries[0].symbol, typeGenerified, VALUE)
            }
        }

        throw LineException("could not resolve property ${expression.identifier}", expression.line)
    }

    fun getParentTypeSymbols(typeSymbol: Symbol, line: Line): List<Symbol> {
        val typeEntry = typeEntryMap.get(typeSymbol, line)
        if (typeEntry.parentTypeSymbols == null) {
            typeEntry.parentTypeSymbols = when (typeEntry) {
                is RsTypeEntryLang -> {
                    if (typeEntry.parentTypeSymbol != null) {
                        listOf(typeSymbol) + getParentTypeSymbols(typeEntry.parentTypeSymbol, line)
                    } else {
                        listOf(typeSymbol)
                    }
                }
                is RsTypeEntryRegular ->{
                    val parent = resolveTypeSymbol(typeEntry.parentIdentifier, typeEntry.scope, line)
                    listOf(typeSymbol) + getParentTypeSymbols(parent, line)
                }
            }
        }
        return typeEntry.parentTypeSymbols!!
    }

    fun getPropertyEvaluateResult(propertySymbol: Symbol, line: Line): RsEvaluateResult? {
        return propertyEntryMap.get(propertySymbol, line).evaluateResult
    }

    fun hasTypeParameters(typeSymbol: Symbol, line: Line): Boolean {
        return typeEntryMap.get(typeSymbol, line).hasTypeParameters
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
            getParentTypeSymbols(receiver.getTypeGenerifiedNotNull().typeSymbol, line)
                .map { RsResolutionEntry(listOf(it), listOf()) }
        } else {
            resolutionTable.resolutionEntries(scopeSymbol, line)
        }
    }
}
