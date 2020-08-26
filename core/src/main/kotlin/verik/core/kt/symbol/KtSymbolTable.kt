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
import verik.core.kt.*
import verik.core.lang.Lang
import verik.core.lang.LangSymbol.SCOPE_LANG
import java.util.concurrent.ConcurrentHashMap

data class KtSymbolTableResolveResult(
        val symbol: Symbol,
        val type: Symbol
)

class KtSymbolTable {

    private val resolutionTable = KtResolutionTable()
    private val scopeTableMap = ConcurrentHashMap<Symbol, KtScopeTable>()

    private val typeEntryMap = ConcurrentHashMap<Symbol, KtTypeEntry>()
    private val functionEntryMap = ConcurrentHashMap<Symbol, KtFunctionEntry>()
    private val operatorEntryMap = ConcurrentHashMap<Symbol, KtOperatorEntry>()
    private val propertyEntryMap = ConcurrentHashMap<Symbol, KtPropertyEntry>()

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
        for (function in Lang.functions) {
            val functionEntry = KtFunctionEntryLang(
                    function.symbol,
                    function.identifier,
                    function.returnType,
                    function.argTypes
            )
            val scope = function.targetType ?: SCOPE_LANG
            addFunctionEntry(functionEntry, scope, 0)
        }
        for (operator in Lang.operators) {
            val operatorEntry = KtOperatorEntry(
                    operator.symbol,
                    operator.identifier,
                    operator.resolver
            )
            addOperatorEntry(operatorEntry)
        }
        for (property in Lang.properties) {
            val propertyEntry = KtPropertyEntryLang(
                    property.symbol,
                    property.identifier,
                    property.type
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

    fun addType(type: KtDeclarationType, scope: Symbol) {
        addTypeEntry(KtTypeEntryRegular(null, type), scope, type.line)
    }

    fun addFunction(function: KtDeclarationFunction, scope: Symbol) {
        addFunctionEntry(KtFunctionEntryRegular(function), scope, function.line)
    }

    fun addProperty(property: KtDeclarationProperty, scope: Symbol) {
        addPropertyEntry(KtPropertyEntryRegular(property), scope, property.line)
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

    fun resolveFunction(expression: KtExpressionFunction, scope: Symbol): KtSymbolTableResolveResult {
        val argsTypes = expression.args.map {
            it.type ?: throw LineException("expression has not been resolved", it)
        }
        val argsParents = argsTypes.map {
            getTypeEntry(it, expression.line).parents
                    ?: throw LineException("type $it has not been resolved", expression)
        }

        val resolutionEntries = if (expression.target != null) {
            val targetType = expression.target.type
                    ?: throw LineException("expression had not been resolved", expression.target)
            listOf(KtResolutionEntry(listOf(targetType)))
        } else {
            resolutionTable.resolutionEntries(scope, expression.line)
        }

        for (resolutionEntry in resolutionEntries) {
            for (resolutionScope in resolutionEntry.scopes) {
                val functionEntries = getScopeTable(resolutionScope, expression.line)
                        .resolveFunction(expression.identifier)
                        .map { getFunctionEntry(it, expression.line) }
                        .filter { it.matches(argsParents) }
                if (functionEntries.isNotEmpty()) {
                    val functionEntry = functionEntries.first()
                    val functionEntryType = functionEntry.returnType
                            ?: throw LineException("function ${expression.identifier} has not been resolved", expression)
                    return KtSymbolTableResolveResult(functionEntry.symbol, functionEntryType)
                }
            }
        }
        throw LineException("could not resolve function ${expression.identifier}", expression)
    }

    fun resolveOperator(expression: KtExpressionOperator): Symbol {
        return getOperatorEntry(expression.operator, expression.line).resolver(expression)
    }

    fun resolveProperty(expression: KtExpressionProperty, scope: Symbol): KtSymbolTableResolveResult {
        val resolutionEntries = if (expression.target != null) {
            val targetType = expression.target.type
                    ?: throw LineException("expression has not been resolved", expression)
            listOf(KtResolutionEntry(listOf(targetType)))
        } else {
            resolutionTable.resolutionEntries(scope, expression.line)
        }

        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val property = getScopeTable(it, expression.line)
                        .resolveProperty(expression.identifier)
                if (property != null) {
                    val propertyEntryType = getPropertyEntry(property, expression.line).type
                            ?: throw LineException("property ${expression.identifier} has not been resolved", expression)
                    return KtSymbolTableResolveResult(property, propertyEntryType)
                }
            }
        }

        throw LineException("could not resolve property ${expression.identifier}", expression)
    }

    private fun addScope(scope: Symbol, parent: Symbol, line: Int) {
        resolutionTable.addScope(scope, parent, line)
        if (scopeTableMap[scope] != null) {
            throw LineException("scope table for $scope has already been defined", line)
        }
        scopeTableMap[scope] = KtScopeTable(scope)
    }

    private fun addTypeEntry(typeEntry: KtTypeEntry, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addType(typeEntry, line)
        if (typeEntryMap[typeEntry.symbol] != null) {
            throw LineException("type ${typeEntry.identifier} has already been defined", line)
        }
        typeEntryMap[typeEntry.symbol] = typeEntry
        addScope(typeEntry.symbol, scope, line)
    }

    private fun addFunctionEntry(functionEntry: KtFunctionEntry, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addFunction(functionEntry, line)
        if (functionEntryMap[functionEntry.symbol] != null) {
            throw LineException("function ${functionEntry.identifier} has already been defined", line)
        }
        functionEntryMap[functionEntry.symbol] = functionEntry
        addScope(functionEntry.symbol, scope, line)
    }

    private fun addOperatorEntry(operatorEntry: KtOperatorEntry) {
        if (operatorEntryMap[operatorEntry.symbol] != null) {
            throw IllegalArgumentException("operator ${operatorEntry.identifier} has already been defined")
        }
        operatorEntryMap[operatorEntry.symbol] = operatorEntry
    }

    private fun addPropertyEntry(propertyEntry: KtPropertyEntry, scope: Symbol, line: Int) {
        getScopeTable(scope, line).addProperty(propertyEntry, line)
        if (propertyEntryMap[propertyEntry.symbol] != null) {
            throw LineException("property ${propertyEntry.identifier} has already been defined", line)
        }
        propertyEntryMap[propertyEntry.symbol] = propertyEntry
    }

    private fun getScopeTable(scope: Symbol, line: Int): KtScopeTable {
        return scopeTableMap[scope]
                ?: throw LineException("scope $scope has not been defined", line)
    }

    private fun getTypeEntry(type: Symbol, line: Int): KtTypeEntry {
        return typeEntryMap[type]
                ?: throw LineException("type $type has not been defined", line)
    }

    private fun getFunctionEntry(function: Symbol, line: Int): KtFunctionEntry {
        return functionEntryMap[function]
                ?: throw LineException("function $function has not been defined", line)
    }

    private fun getOperatorEntry(operator: Symbol, line: Int): KtOperatorEntry {
        return operatorEntryMap[operator]
                ?: throw LineException("operator $operator has not been defined", line)
    }

    private fun getPropertyEntry(property: Symbol, line: Int): KtPropertyEntry {
        return propertyEntryMap[property]
                ?: throw LineException("property $property has not been defined", line)
    }
}
