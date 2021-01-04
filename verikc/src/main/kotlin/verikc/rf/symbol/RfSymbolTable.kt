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

package verikc.rf.symbol

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.LangDeclaration
import verikc.rf.ast.*

class RfSymbolTable {

    private val typeEntryMap = SymbolEntryMap<RfTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<RfFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<RfOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<RfPropertyEntry>("property")

    init {
        for (type in LangDeclaration.types) {
            val typeEntry = RfTypeEntry(
                type.symbol,
                type.identifier
            )
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = RfFunctionLangEntry(
                function.symbol,
                function.argTypeClasses,
                function.isVararg,
                function.reifier
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = RfOperatorEntry(
                operator.symbol,
                operator.reifier
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addFunction(enum: RfEnum) {
        val functionEntry = RfFunctionLangEntry(
            enum.typeConstructorFunctionSymbol,
            listOf(),
            false
        ) { enum.symbol.toTypeReifiedType() }
        functionEntryMap.add(functionEntry, enum.line)
    }

    fun addFunction(methodBlock: RfMethodBlock) {
        val argTypesReified = methodBlock.parameterProperties.map {
            it.typeReified ?: throw LineException("parameter ${it.symbol} has not been reified", it.line)
        }
        val returnTypeReified = methodBlock.returnTypeReified
            ?: throw LineException("function ${methodBlock.symbol} return value has not been reified", methodBlock.line)

        functionEntryMap.add(
            RfFunctionRegularEntry(methodBlock.symbol, argTypesReified, returnTypeReified),
            methodBlock.line
        )
    }

    fun addProperty(enum: RfEnum) {
        val propertyEntry = RfPropertyEntry(
            enum.symbol,
            enum.symbol.toTypeReifiedType()
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
        return when (val functionEntry = functionEntryMap.get(expression.functionSymbol, expression.line)) {
            is RfFunctionLangEntry -> {
                for (i in expression.args.indices) {
                    if (expression.args[i].getTypeReifiedNotNull().typeClass != functionEntry.getArgTypeClass(i))
                        throw LineException("type class mismatch when resolving argument ${i+1} of function ${
                            expression.functionSymbol}", expression.line)
                }
                functionEntry.reifier(expression)
                    ?: throw LineException("unable to reify function ${expression.functionSymbol}", expression.line)
            }
            is RfFunctionRegularEntry -> {
                for (i in expression.args.indices) {
                    val typeReified = expression.args[i].getTypeReifiedNotNull()
                    if (typeReified.typeClass != INSTANCE)
                        throw LineException("type expression not permitted here", expression.line)
                    if (typeReified != functionEntry.argTypesReified[i])
                        throw LineException("type mismatch when resolving argument ${
                            i+1} of function ${expression.functionSymbol} expected ${
                                functionEntry.argTypesReified[i]} but got $typeReified", expression.line)
                }
                functionEntry.returnTypeReified
            }
        }
    }

    fun reifyOperator(expression: RfExpressionOperator): TypeReified {
        return operatorEntryMap.get(expression.operatorSymbol, expression.line).reifier(expression)
            ?: throw LineException("unable to reify operator ${expression.operatorSymbol}", expression.line)
    }
}
