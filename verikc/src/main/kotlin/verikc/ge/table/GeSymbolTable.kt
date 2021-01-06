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

package verikc.ge.table

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.SymbolEntryMap
import verikc.ge.ast.*
import verikc.lang.LangDeclaration

class GeSymbolTable {

    private val typeEntryMap = SymbolEntryMap<GeTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<GeFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<GeOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<GePropertyEntry>("property")

    init {
        for (type in LangDeclaration.types) {
            val typeEntry = GeTypeEntry(
                type.symbol,
                type.identifier
            )
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = GeFunctionLangEntry(
                function.symbol,
                function.argTypeClasses,
                function.isVararg,
                function.generifier
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = GeOperatorEntry(
                operator.symbol,
                operator.generifier
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addFunction(enum: GeEnum) {
        val functionEntry = GeFunctionLangEntry(
            enum.typeConstructorFunctionSymbol,
            listOf(),
            false
        ) { enum.symbol.toTypeGenerifiedType() }
        functionEntryMap.add(functionEntry, enum.line)
    }

    fun addFunction(methodBlock: GeMethodBlock) {
        val argTypesGenerified = methodBlock.parameterProperties.map {
            it.typeGenerified ?: throw LineException("parameter ${it.symbol} has not been generified", it.line)
        }
        val returnTypeGenerified = methodBlock.returnTypeGenerified
            ?: throw LineException(
                "function ${methodBlock.symbol} return value has not been generified",
                methodBlock.line
            )

        functionEntryMap.add(
            GeFunctionRegularEntry(methodBlock.symbol, argTypesGenerified, returnTypeGenerified),
            methodBlock.line
        )
    }

    fun addProperty(enum: GeEnum) {
        val propertyEntry = GePropertyEntry(
            enum.symbol,
            enum.symbol.toTypeGenerifiedType()
        )
        propertyEntryMap.add(propertyEntry, enum.line)
    }

    fun addProperty(property: GeProperty) {
        val typeGenerified = property.typeGenerified
            ?: throw LineException("property ${property.symbol} has not been generified", property.line)
        propertyEntryMap.add(GePropertyEntry(property.symbol, typeGenerified), property.line)
    }

    fun generifyProperty(expression: GeExpressionProperty): TypeGenerified {
        return propertyEntryMap.get(expression.propertySymbol, expression.line).typeGenerified
    }

    fun generifyFunction(expression: GeExpressionFunction): TypeGenerified {
        return when (val functionEntry = functionEntryMap.get(expression.functionSymbol, expression.line)) {
            is GeFunctionLangEntry -> {
                for (i in expression.args.indices) {
                    if (expression.args[i].getTypeGenerifiedNotNull().typeClass != functionEntry.getArgTypeClass(i))
                        throw LineException("type class mismatch when resolving argument ${i+1} of function ${
                            expression.functionSymbol}", expression.line)
                }
                functionEntry.generifier(expression)
                    ?: throw LineException("unable to generify function ${expression.functionSymbol}", expression.line)
            }
            is GeFunctionRegularEntry -> {
                for (i in expression.args.indices) {
                    val typeGenerified = expression.args[i].getTypeGenerifiedNotNull()
                    if (typeGenerified.typeClass != INSTANCE)
                        throw LineException("type expression not permitted here", expression.line)
                    if (typeGenerified != functionEntry.argTypesGenerified[i])
                        throw LineException("type mismatch when resolving argument ${
                            i+1} of function ${expression.functionSymbol} expected ${
                                functionEntry.argTypesGenerified[i]} but got $typeGenerified", expression.line)
                }
                functionEntry.returnTypeGenerified
            }
        }
    }

    fun generifyOperator(expression: GeExpressionOperator): TypeGenerified {
        return operatorEntryMap.get(expression.operatorSymbol, expression.line).generifier(expression)
            ?: throw LineException("unable to generify operator ${expression.operatorSymbol}", expression.line)
    }
}
