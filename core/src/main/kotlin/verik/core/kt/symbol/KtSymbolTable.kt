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
import verik.core.base.SymbolEntryMap
import verik.core.kt.*
import verik.core.lang.Lang
import verik.core.lang.LangSymbol.SCOPE_LANG
import verik.core.lang.LangSymbol.TYPE_ENUM

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

    fun addFile(file: Symbol, resolutionEntries: List<KtResolutionEntry>) {
        if (!file.isFileSymbol()) {
            throw LineException("file expected but got $file", 0)
        }
        resolutionTable.addFile(file, resolutionEntries)
        scopeTableMap.add(KtScopeTable(file), 0)
    }

    fun addScope(scope: Symbol, parent: Symbol, line: Int) {
        resolutionTable.addScope(scope, parent, line)
        scopeTableMap.add(KtScopeTable(scope), line)
    }

    fun addType(type: KtPrimaryType, scope: Symbol) {
        val typeEntry = KtTypeEntryRegular(
                type.symbol,
                type.identifier,
                null,
                type.constructorInvocation.typeIdentifier,
                scope
        )
        addTypeEntry(typeEntry, scope, type.line)
    }

    fun addFunction(function: KtFunction, scope: Symbol) {
        val returnType = function.returnType
                ?: throw LineException("function return type has not been resolved", function.line)
        val argTypes = function.parameters.map {
            it.type ?: throw LineException("function argument ${it.identifier} has not been resolved", function.line)
        }
        val functionEntry = KtFunctionEntry(
                function.symbol,
                function.identifier,
                returnType,
                argTypes
        )
        addFunctionEntry(functionEntry, scope, function.line)
    }

    fun addFunction(type: KtPrimaryType, scope: Symbol) {
        val argTypes = type.parameters.map {
            it.type ?: throw LineException("type argument ${it.identifier} has not been resolved", type.line)
        }
        val functionEntry = if (type.constructorInvocation.type == TYPE_ENUM) {
            KtFunctionEntry(
                    type.symbol,
                    type.identifier,
                    type.symbol,
                    listOf()
            )
        } else {
            KtFunctionEntry(
                    type.symbol,
                    type.identifier,
                    type.symbol,
                    argTypes
            )
        }
        addFunctionEntry(functionEntry, scope, type.line)
    }

    fun addProperty(property: KtProperty, scope: Symbol) {
        val type = property.type
                ?: throw LineException("property ${property.identifier} has not been resolved", property)
        val propertyEntry = KtPropertyEntry(
                property.symbol,
                property.identifier,
                type
        )
        addPropertyEntry(propertyEntry, scope, property.line)
    }

    fun resolveType(identifier: String, scope: Symbol, line: Int): Symbol {
        val resolutionEntries = resolutionTable.resolutionEntries(scope, line)
        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val type = scopeTableMap.get(it, line).resolveType(identifier)
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
            getParents(it, expression.line)
        }

        val resolutionEntries = getResolutionEntries(expression.receiver, scope, expression.line)

        for (resolutionEntry in resolutionEntries) {
            for (resolutionScope in resolutionEntry.scopes) {
                val functionEntries = scopeTableMap.get(resolutionScope, expression.line)
                        .resolveFunction(expression.identifier)
                        .map { functionEntryMap.get(it, expression.line) }
                        .filter { it.matches(argsParents) }
                if (functionEntries.isNotEmpty()) {
                    val functionEntry = functionEntries.first()
                    return KtSymbolTableResolveResult(functionEntry.symbol, functionEntry.returnType)
                }
            }
        }

        throw LineException("could not resolve function ${expression.identifier}", expression)
    }

    fun resolveOperator(expression: KtExpressionOperator): Symbol {
        return operatorEntryMap.get(expression.operator, expression.line).resolver(expression)
    }

    fun resolveProperty(expression: KtExpressionProperty, scope: Symbol): KtSymbolTableResolveResult {
        val resolutionEntries = getResolutionEntries(expression.receiver, scope, expression.line)

        for (resolutionEntry in resolutionEntries) {
            resolutionEntry.scopes.forEach {
                val property = scopeTableMap.get(it, expression.line)
                        .resolveProperty(expression.identifier)
                if (property != null) {
                    return KtSymbolTableResolveResult(
                            property,
                            propertyEntryMap.get(property, expression.line).type
                    )
                }
            }
        }

        throw LineException("could not resolve property ${expression.identifier}", expression)
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
                    type.parent
            )
            addScope(type.symbol, SCOPE_LANG, 0)
            addTypeEntry(typeEntry, SCOPE_LANG, 0)
        }
        for (function in Lang.functions) {
            val functionEntry = KtFunctionEntry(
                    function.symbol,
                    function.identifier,
                    function.returnType,
                    function.argTypes
            )
            val scope = function.receiverType ?: SCOPE_LANG
            addFunctionEntry(functionEntry, scope, 0)
        }
        for (operator in Lang.operators) {
            val operatorEntry = KtOperatorEntry(
                    operator.symbol,
                    operator.identifier,
                    operator.resolver
            )
            operatorEntryMap.add(operatorEntry, 0)
        }
        for (property in Lang.properties) {
            val propertyEntry = KtPropertyEntry(
                    property.symbol,
                    property.identifier,
                    property.type
            )
            addPropertyEntry(propertyEntry, SCOPE_LANG, 0)
        }
    }

    private fun addTypeEntry(typeEntry: KtTypeEntry, scope: Symbol, line: Int) {
        scopeTableMap.get(scope, line).addType(typeEntry, line)
        typeEntryMap.add(typeEntry, line)
    }

    private fun addFunctionEntry(functionEntry: KtFunctionEntry, scope: Symbol, line: Int) {
        scopeTableMap.get(scope, line).addFunction(functionEntry, line)
        functionEntryMap.add(functionEntry, line)
    }

    private fun addPropertyEntry(propertyEntry: KtPropertyEntry, scope: Symbol, line: Int) {
        scopeTableMap.get(scope, line).addProperty(propertyEntry, line)
        propertyEntryMap.add(propertyEntry, line)
    }

    private fun getResolutionEntries(receiver: KtExpression?, scope: Symbol, line: Int): List<KtResolutionEntry> {
        return if (receiver != null) {
            val receiverType = receiver.type
                    ?: throw LineException("expression receiver has not been resolved", line)
            getParents(receiverType, line)
                    .map { KtResolutionEntry(listOf(it)) }
        } else {
            resolutionTable.resolutionEntries(scope, line)
        }
    }

    private fun getParents(type: Symbol, line: Int): List<Symbol> {
        val typeEntry = typeEntryMap.get(type, line)
        return typeEntry.parents ?: when (typeEntry) {
            is KtTypeEntryRegular -> {
                val parent = resolveType(typeEntry.parentIdentifier, typeEntry.scope, line)
                listOf(type) + getParents(parent, line)
            }
            is KtTypeEntryLang -> {
                if (typeEntry.parent != null) {
                    listOf(type) + getParents(typeEntry.parent, line)
                } else {
                    listOf(type)
                }
            }
        }
    }
}
