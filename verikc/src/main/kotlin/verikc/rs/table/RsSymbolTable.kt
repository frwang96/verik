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
import verikc.lang.util.LangIdentifierUtil
import verikc.rs.ast.*
import verikc.rs.resolve.RsEvaluateResult
import verikc.rs.resolve.RsResolverFunctionUtil

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
                type.parentTypeSymbol,
                type.hasTypeParameters,
                null
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
            type.typeParent.typeIdentifier,
            scopeSymbol,
            null
        )
        addTypeEntry(typeEntry, scopeSymbol, type.line)
    }

    fun addTypeAlias(typeAlias: RsTypeAlias, scopeSymbol: Symbol) {
        val parentIdentifier = if (typeAlias.expression is RsExpressionFunction) {
            LangIdentifierUtil.typeIdentifier(typeAlias.expression.identifier)
                ?: throw LineException("type constructor expression expected", typeAlias.line)
        } else throw LineException("type constructor expression expected", typeAlias.line)

        val typeEntry = RsTypeEntryAlias(
            typeAlias.symbol,
            typeAlias.identifier,
            parentIdentifier,
            scopeSymbol,
            null,
            null
        )
        addTypeEntry(typeEntry, scopeSymbol, typeAlias.line)
    }

    fun setTypeAlias(typeAlias: RsTypeAlias) {
        val typeEntry = typeEntryMap.get(typeAlias.symbol, typeAlias.line)
        if (typeEntry is RsTypeEntryAlias) {
            typeEntry.typeGenerified = typeAlias.getTypeGenerifiedNotNull()
        } else throw LineException("type alias expected in type entry map", typeAlias.line)
    }

    fun addFunction(
        function: RsFunction,
        argTypeSymbols: List<Symbol>,
        expressionClass: ExpressionClass,
        scopeSymbol: Symbol
    ) {
        val functionEntry = RsFunctionEntryRegular(
            function.symbol,
            function.identifier,
            argTypeSymbols,
            List(argTypeSymbols.size) { VALUE },
            false,
            expressionClass,
            null,
            null
        )
        addFunctionEntry(functionEntry, scopeSymbol, function.line)
    }

    fun setFunction(function: RsFunction) {
        val functionEntry = functionEntryMap.get(function.symbol, function.line)
        if (functionEntry is RsFunctionEntryRegular) {
            functionEntry.argTypesGenerified = function.parameterProperties.mapIndexed { index, property ->
                val typeSymbol = functionEntry.argTypeSymbols[index]
                val typeGenerified = property.getTypeGenerifiedNotNull()
                if (typeGenerified.typeSymbol != typeSymbol)
                    throw LineException(
                        "expected type $typeSymbol but got ${typeGenerified.typeSymbol} for ${property.symbol}",
                        property.line
                    )
                typeGenerified
            }
            functionEntry.returnTypeGenerified = function.getReturnTypeGenerifiedNotNull()
        } else throw LineException("regular function entry expected", function.line)
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

    fun resolveType(identifier: String, scopeSymbol: Symbol, line: Line): RsTypeResult {
        val resolutionEntries = resolutionTable.resolutionEntries(scopeSymbol, line)
        for (resolutionEntry in resolutionEntries) {
            val typeEntries = ArrayList<RsTypeEntry>()
            resolutionEntry.scopeSymbols.forEach {
                val typeSymbol = scopeTableMap.get(it, line).resolveTypeSymbol(identifier)
                if (typeSymbol != null) {
                    typeEntries.add(typeEntryMap.get(typeSymbol, line))
                }
            }
            resolutionEntry.declarationSymbols.forEach {
                if (it in typeEntryMap) {
                    val typeEntry = typeEntryMap.get(it, line)
                    if (typeEntry.identifier == identifier) {
                        typeEntries.add(typeEntry)
                    }
                }
            }
            if (typeEntries.isNotEmpty()) {
                if (typeEntries.size > 1)
                    throw LineException("could not resolve type ambiguity for $identifier", line)
                val typeEntry = typeEntries[0]
                val symbol = typeEntry.symbol
                return when (typeEntry) {
                    is RsTypeEntryLang -> {
                        val typeGenerified = if (typeEntry.hasTypeParameters) null else symbol.toTypeGenerified()
                        RsTypeResult(symbol, false, symbol, typeGenerified)
                    }
                    is RsTypeEntryRegular -> {
                        RsTypeResult(symbol, false, symbol, symbol.toTypeGenerified())
                    }
                    is RsTypeEntryAlias -> {
                        if (typeEntry.typeSymbol == null) {
                            val typeResult = resolveType(typeEntry.parentIdentifier, typeEntry.scopeSymbol, line)
                            typeEntry.typeSymbol = typeResult.typeSymbol
                        }
                        RsTypeResult(symbol, true, typeEntry.getTypeSymbolNotNull(line), typeEntry.typeGenerified)
                    }
                }
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
                            ?: throw RsResolveException(functionEntry.symbol, expression.line)
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
                    ?: throw RsResolveException(propertyEntries[0].symbol, expression.line)
                return RsResolverResult(propertyEntries[0].symbol, typeGenerified, VALUE)
            }
        }

        throw LineException("could not resolve property ${expression.identifier}", expression.line)
    }

    fun getParentTypeSymbols(typeSymbol: Symbol, line: Line): List<Symbol> {
        return when (val typeEntry = typeEntryMap.get(typeSymbol, line)) {
            is RsTypeEntryLang -> {
                if (typeEntry.parentTypeSymbols == null) {
                    typeEntry.parentTypeSymbols = if (typeEntry.parentTypeSymbol != null) {
                        listOf(typeSymbol) + getParentTypeSymbols(typeEntry.parentTypeSymbol, line)
                    } else {
                        listOf(typeSymbol)
                    }
                }
                typeEntry.parentTypeSymbols!!
            }
            is RsTypeEntryRegular -> {
                if (typeEntry.parentTypeSymbols == null) {
                    val parentSymbol = resolveType(typeEntry.parentIdentifier, typeEntry.scopeSymbol, line).typeSymbol
                    typeEntry.parentTypeSymbols = listOf(typeSymbol) + getParentTypeSymbols(parentSymbol, line)
                }
                typeEntry.parentTypeSymbols!!
            }
            is RsTypeEntryAlias ->
                throw LineException("parent type symbols should not be accessed through type alias", line)
        }
    }

    fun getPropertyEvaluateResult(propertySymbol: Symbol, line: Line): RsEvaluateResult? {
        return propertyEntryMap.get(propertySymbol, line).evaluateResult
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
