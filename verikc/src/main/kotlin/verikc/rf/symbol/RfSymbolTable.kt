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
import verikc.base.ast.*
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
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in Lang.functions) {
            val functionEntry = RfFunctionEntry(
                function.symbol,
                function.argTypeClasses,
                function.reifier
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in Lang.operators) {
            val operatorEntry = RfOperatorEntry(
                operator.symbol,
                operator.reifier
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addFunction(enum: RfEnum) {
        val functionEntry = RfFunctionEntry(
            enum.typeConstructorFunctionSymbol,
            listOf()
        ) { TypeReified(enum.symbol, TypeClass.TYPE, listOf()) }
        functionEntryMap.add(functionEntry, enum.line)
    }

    fun addComponent(module: RfModule) {
        val componentEntry = RfComponentEntry(
            module.symbol,
            module.identifier,
            module.ports
        )
        componentEntryMap.add(componentEntry, module.line)
    }

    fun addProperty(enum: RfEnum) {
        val propertyEntry = RfPropertyEntry(
            enum.symbol,
            TypeReified(enum.symbol, TypeClass.TYPE, listOf())
        )
        propertyEntryMap.add(propertyEntry, enum.line)
    }

    fun addProperty(property: RfProperty) {
        val typeReified = property.typeReified
            ?: throw LineException("property ${property.symbol} has not been reified", property.line)
        propertyEntryMap.add(RfPropertyEntry(property.symbol, typeReified), property.line)
    }

    fun reifyProperty(expression: RfExpressionProperty): TypeReified {
        return propertyEntryMap.get(expression.propertySymbol, expression.line).typeReified
    }

    fun reifyFunction(expression: RfExpressionFunction): TypeReified {
        val functionEntry = functionEntryMap.get(expression.functionSymbol, expression.line)
        if (expression.args.map { it.typeReified!!.typeClass } != functionEntry.argTypeClasses) {
            throw LineException(
                "type class mismatch when resolving function ${expression.functionSymbol}",
                expression.line
            )
        }
        return functionEntry.reifier(expression)
            ?: throw LineException("unable to reify function ${expression.functionSymbol}", expression.line)
    }

    fun reifyOperator(expression: RfExpressionOperator): TypeReified {
        return operatorEntryMap.get(expression.operatorSymbol, expression.line).reifier(expression)
            ?: throw LineException("unable to reify operator ${expression.operatorSymbol}", expression.line)
    }

    fun getComponentPorts(typeSymbol: Symbol, line: Line): List<RfPort> {
        return componentEntryMap.get(typeSymbol, line).ports
    }
}
