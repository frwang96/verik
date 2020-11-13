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

package verikc.rf.symbol

import verikc.base.SymbolEntryMap
import verikc.base.ast.LineException
import verikc.base.ast.ReifiedType
import verikc.base.ast.Symbol
import verikc.lang.Lang
import verikc.rf.ast.*

class RfSymbolTable {

    private val typeEntryMap = SymbolEntryMap<RfTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<RfFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<RfOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<RfPropertyEntry>("property")
    private val componentEntryMap = SymbolEntryMap<RfComponentEntry>("component")

    init {
        for (type in Lang.types) {
            val typeEntry = RfTypeEntry(
                    type.symbol,
                    type.identifier
            )
            typeEntryMap.add(typeEntry, 0)
        }
        for (function in Lang.functions) {
            val functionEntry = RfFunctionEntry(
                    function.symbol,
                    function.argTypeClasses,
                    function.reifier
            )
            functionEntryMap.add(functionEntry, 0)
        }
        for (operator in Lang.operators) {
            val operatorEntry = RfOperatorEntry(
                    operator.symbol,
                    operator.reifier
            )
            operatorEntryMap.add(operatorEntry, 0)
        }
    }

    fun addComponent(module: RfModule) {
        val componentEntry = RfComponentEntry(
                module.symbol,
                module.identifier,
                module.ports
        )
        componentEntryMap.add(componentEntry, module.line)
    }

    fun addProperty(property: RfProperty) {
        propertyEntryMap.add(RfPropertyEntry(property.symbol, property), property.line)
    }

    fun reifyProperty(expression: RfExpressionProperty): ReifiedType {
        return propertyEntryMap.get(expression.property, expression.line).property.reifiedType
                ?: throw LineException("property ${expression.property} has not been reified", expression)
    }

    fun reifyFunction(expression: RfExpressionFunction): ReifiedType {
        val functionEntry = functionEntryMap.get(expression.function, expression.line)
        if (expression.args.map { it.reifiedType!!.typeClass } != functionEntry.argTypeClasses) {
            throw LineException("type class mismatch when resolving function ${expression.function}", expression)
        }
        return functionEntry.reifier(expression)
                ?: throw LineException("unable to reify function ${expression.function}", expression)
    }

    fun reifyOperator(expression: RfExpressionOperator): ReifiedType {
        return operatorEntryMap.get(expression.operator, expression.line).reifier(expression)
                ?: throw LineException("unable to reify operator ${expression.operator}", expression)
    }

    fun getComponentPorts(type: Symbol, line: Int): List<RfPort> {
        return componentEntryMap.get(type, line).ports
    }
}
