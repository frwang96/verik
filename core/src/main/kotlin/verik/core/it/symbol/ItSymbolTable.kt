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

package verik.core.it.symbol

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.it.*
import verik.core.lang.Lang
import verik.core.lang.LangFunctionExtractorRequest
import verik.core.lang.LangOperatorExtractorRequest
import verik.core.sv.SvReifiedType
import verik.core.sv.SvStatement
import java.util.concurrent.ConcurrentHashMap

class ItSymbolTable {

    private val typeEntryMap = ConcurrentHashMap<Symbol, ItTypeEntry>()
    private val functionEntryMap = ConcurrentHashMap<Symbol, ItFunctionEntry>()
    private val operatorEntryMap = ConcurrentHashMap<Symbol, ItOperatorEntry>()
    private val propertyEntryMap = ConcurrentHashMap<Symbol, ItPropertyEntry>()

    init {
        for (type in Lang.types) {
            val typeEntry = ItTypeEntry(
                    type.symbol,
                    type.extractor
            )
            addTypeEntry(typeEntry, 0)
        }
        for (function in Lang.functions) {
            val functionEntry = ItFunctionEntry(
                    function.symbol,
                    function.reifier,
                    function.extractor
            )
            addFunctionEntry(functionEntry, 0)
        }
        for (operator in Lang.operators) {
            val operatorEntry = ItOperatorEntry(
                    operator.symbol,
                    operator.reifier,
                    operator.extractor
            )
            addOperatorEntry(operatorEntry)
        }
    }

    fun addProperty(property: ItProperty) {
        addPropertyEntry(ItPropertyEntry(property.symbol, property), property.line)
    }

    fun reifyProperty(expression: ItExpressionProperty): ItReifiedType {
        return getProperty(expression.property, expression.line).property.reifiedType
                ?: throw LineException("property ${expression.property} has not been reified", expression)
    }

    fun reifyFunction(expression: ItExpressionFunction) {
        getFunction(expression.function, expression.line).reifier(expression)
    }

    fun reifyOperator(expression: ItExpressionOperator): ItReifiedType {
        return getOperator(expression.operator, expression.line).reifier(expression)
    }

    fun extractType(reifiedType: ItReifiedType, line: Int): SvReifiedType {
        if (reifiedType.typeClass != ItTypeClass.INSTANCE) {
                throw LineException("unable to extract type $reifiedType due to invalid type class", line)
        }
        return getType(reifiedType.type, line).extractor(reifiedType)
                ?: throw LineException("unable to extract type $reifiedType", line)
    }

    fun extractFunction(request: LangFunctionExtractorRequest): SvStatement {
        val function = request.function
        return getFunction(function.function, function.line).extractor(request)
                ?: throw LineException("unable to extract function ${function.function}", function)
    }

    fun extractOperator(request: LangOperatorExtractorRequest): SvStatement {
        val operator = request.operator
        return getOperator(operator.operator, operator.line).extractor(request)
                ?: throw LineException("unable to extract operator ${operator.operator}", operator)
    }

    fun extractProperty(expression: ItExpressionProperty): String {
        return getProperty(expression.property, expression.line).property.identifier
    }

    private fun addTypeEntry(typeEntry: ItTypeEntry, line: Int) {
        if (typeEntryMap[typeEntry.symbol] != null) {
            throw LineException("type ${typeEntry.symbol} has already been defined", line)
        }
        typeEntryMap[typeEntry.symbol] = typeEntry
    }

    private fun addFunctionEntry(functionEntry: ItFunctionEntry, line: Int) {
        if (functionEntryMap[functionEntry.symbol] != null) {
            throw LineException("function ${functionEntry.symbol} has already been defined", line)
        }
        functionEntryMap[functionEntry.symbol] = functionEntry
    }

    private fun addOperatorEntry(operatorEntry: ItOperatorEntry) {
        if (operatorEntryMap[operatorEntry.symbol] != null) {
            throw IllegalArgumentException("operator ${operatorEntry.symbol} has already been defined")
        }
        operatorEntryMap[operatorEntry.symbol] = operatorEntry
    }

    private fun addPropertyEntry(propertyEntry: ItPropertyEntry, line: Int) {
        if (propertyEntryMap[propertyEntry.symbol] != null) {
            throw LineException("property ${propertyEntry.symbol} has already been defined", line)
        }
        propertyEntryMap[propertyEntry.symbol] = propertyEntry
    }

    private fun getType(type: Symbol, line: Int): ItTypeEntry {
        return typeEntryMap[type]
                ?: throw LineException("type $type has not been defined", line)
    }

    private fun getFunction(function: Symbol, line: Int): ItFunctionEntry {
        return functionEntryMap[function]
                ?: throw LineException("function $function has not been defined", line)
    }

    private fun getOperator(operator: Symbol, line: Int): ItOperatorEntry {
        return operatorEntryMap[operator]
                ?: throw LineException("operator $operator has not been defined", line)
    }

    private fun getProperty(property: Symbol, line: Int): ItPropertyEntry {
        return propertyEntryMap[property]
                ?: throw LineException("property $property has not been defined", line)
    }
}