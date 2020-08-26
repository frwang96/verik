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
import verik.core.base.SymbolEntryMap
import verik.core.it.*
import verik.core.lang.Lang
import verik.core.lang.LangFunctionExtractorRequest
import verik.core.lang.LangOperatorExtractorRequest
import verik.core.sv.SvReifiedType
import verik.core.sv.SvStatement

class ItSymbolTable {

    private val typeEntryMap = SymbolEntryMap<ItTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<ItFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<ItOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<ItPropertyEntry>("property")

    init {
        for (type in Lang.types) {
            val typeEntry = ItTypeEntry(
                    type.symbol,
                    type.extractor
            )
            typeEntryMap.add(typeEntry, 0)
        }
        for (function in Lang.functions) {
            val functionEntry = ItFunctionEntry(
                    function.symbol,
                    function.reifier,
                    function.extractor
            )
            functionEntryMap.add(functionEntry, 0)
        }
        for (operator in Lang.operators) {
            val operatorEntry = ItOperatorEntry(
                    operator.symbol,
                    operator.reifier,
                    operator.extractor
            )
            operatorEntryMap.add(operatorEntry, 0)
        }
    }

    fun addProperty(property: ItProperty) {
        propertyEntryMap.add(ItPropertyEntry(property.symbol, property), property.line)
    }

    fun reifyProperty(expression: ItExpressionProperty): ItReifiedType {
        return propertyEntryMap.get(expression.property, expression.line).property.reifiedType
                ?: throw LineException("property ${expression.property} has not been reified", expression)
    }

    fun reifyFunction(expression: ItExpressionFunction) {
        functionEntryMap.get(expression.function, expression.line).reifier(expression)
    }

    fun reifyOperator(expression: ItExpressionOperator): ItReifiedType {
        return operatorEntryMap.get(expression.operator, expression.line).reifier(expression)
    }

    fun extractType(reifiedType: ItReifiedType, line: Int): SvReifiedType {
        if (reifiedType.typeClass != ItTypeClass.INSTANCE) {
                throw LineException("unable to extract type $reifiedType invalid type class", line)
        }
        return typeEntryMap.get(reifiedType.type, line).extractor(reifiedType)
                ?: throw LineException("unable to extract type $reifiedType", line)
    }

    fun extractFunction(request: LangFunctionExtractorRequest): SvStatement {
        val function = request.function
        return functionEntryMap.get(function.function, function.line).extractor(request)
                ?: throw LineException("unable to extract function ${function.function}", function)
    }

    fun extractOperator(request: LangOperatorExtractorRequest): SvStatement {
        val operator = request.operator
        return operatorEntryMap.get(operator.operator, operator.line).extractor(request)
                ?: throw LineException("unable to extract operator ${operator.operator}", operator)
    }

    fun extractProperty(expression: ItExpressionProperty): String {
        return propertyEntryMap.get(expression.property, expression.line).property.identifier
    }
}