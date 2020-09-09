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
import verik.core.base.SymbolEntryMap
import verik.core.it.*
import verik.core.lang.Lang
import verik.core.sv.SvReifiedType
import verik.core.sv.SvStatement
import verik.core.sv.SvStatementExpression

class ItSymbolTable {

    private val typeEntryMap = SymbolEntryMap<ItTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<ItFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<ItOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<ItPropertyEntry>("property")
    private val componentEntryMap = SymbolEntryMap<ItComponentEntry>("component")

    init {
        for (type in Lang.types) {
            val typeEntry = ItTypeEntry(
                    type.symbol,
                    type.identifier,
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

    fun addComponent(module: ItModule) {
        val componentEntry = ItComponentEntry(
                module.symbol,
                module.identifier,
                module.ports
        )
        componentEntryMap.add(componentEntry, module.line)
    }

    fun addProperty(property: ItProperty) {
        propertyEntryMap.add(ItPropertyEntry(property.symbol, property), property.line)
    }

    fun reifyProperty(expression: ItExpressionProperty): ItReifiedType {
        return propertyEntryMap.get(expression.property, expression.line).property.reifiedType
                ?: throw LineException("property ${expression.property} has not been reified", expression)
    }

    fun reifyFunction(expression: ItExpressionFunction): ItReifiedType {
        return functionEntryMap.get(expression.function, expression.line).reifier(expression)
                ?: throw LineException("unable to reify function ${expression.function}", expression)
    }

    fun reifyOperator(expression: ItExpressionOperator): ItReifiedType {
        return operatorEntryMap.get(expression.operator, expression.line).reifier(expression)
                ?: throw LineException("unable to reify operator ${expression.operator}", expression)
    }

    fun extractType(reifiedType: ItReifiedType, line: Int): SvReifiedType {
        if (reifiedType.typeClass != ItTypeClass.INSTANCE) {
                throw LineException("unable to extract type $reifiedType invalid type class", line)
        }
        return typeEntryMap.get(reifiedType.type, line).extractor(reifiedType)
                ?: throw LineException("unable to extract type $reifiedType", line)
    }

    fun extractFunction(request: ItFunctionExtractorRequest): SvStatement {
        val function = request.function
        return functionEntryMap.get(function.function, function.line).extractor(request)
                ?: throw LineException("unable to extract function ${function.function}", function)
    }

    fun extractOperator(request: ItOperatorExtractorRequest): SvStatement {
        val operator = request.operator
        return operatorEntryMap.get(operator.operator, operator.line).extractor(request)
                ?: throw LineException("unable to extract operator ${operator.operator}", operator)
    }

    fun extractProperty(expression: ItExpressionProperty): SvStatement {
        return SvStatementExpression.wrapProperty(
                expression.line,
                null,
                extractPropertyIdentifier(expression.property, expression.line)
        )
    }

    fun extractPropertyIdentifier(property: Symbol, line: Int): String {
        return propertyEntryMap.get(property, line).property.identifier
    }

    fun extractComponentIdentifier(type: Symbol, line: Int): String {
        return componentEntryMap.get(type, line).identifier.substring(1)
    }
}