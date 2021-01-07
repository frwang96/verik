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

package verikc.gex.table

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeClass
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.SymbolEntryMap
import verikc.gex.ast.*
import verikc.lang.LangDeclaration

class GexSymbolTable {

    private val typeEntryMap = SymbolEntryMap<GexTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<GexFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<GexOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<GexPropertyEntry>("property")

    init {
        for (type in LangDeclaration.types) {
            val typeEntry = GexTypeEntry(
                type.symbol,
                type.identifier
            )
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = GexFunctionLangEntry(
                function.symbol,
                function.argTypeClasses,
                function.isVararg,
                function.generifier
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = GexOperatorEntry(operator.symbol, operator.generifier)
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addFunction(function: GexFunction) {
        val functionEntry = GexFunctionRegularEntry(
            function.symbol,
            function.parameterProperties.map { it.getTypeGenerifiedNotNull() },
            function.getReturnTypeGenerifiedNotNull()
        )
        functionEntryMap.add(functionEntry, function.line)
    }

    fun addProperty(property: GexProperty) {
        val propertyEntry = GexPropertyEntry(property.symbol, property.getTypeGenerifiedNotNull())
        propertyEntryMap.add(propertyEntry, property.line)
    }

    fun addProperty(type: GexType) {
        val propertyEntry = GexPropertyEntry(type.symbol, type.symbol.toTypeGenerifiedType())
        propertyEntryMap.add(propertyEntry, type.line)
    }

    fun generifyProperty(expression: GexExpressionProperty): TypeGenerified {
        return propertyEntryMap.get(expression.propertySymbol, expression.line).typeGenerified
    }

    fun generifyFunction(expression: GexExpressionFunction): TypeGenerified {
        return when (val functionEntry = functionEntryMap.get(expression.functionSymbol, expression.line)) {
            is GexFunctionLangEntry -> generifyFunctionLang(expression, functionEntry)
            is GexFunctionRegularEntry -> generifyFunctionRegular(expression, functionEntry)
        }
    }

    fun generifyOperator(expression: GexExpressionOperator): TypeGenerified {
        return operatorEntryMap.get(expression.operatorSymbol, expression.line).generifier(expression)
            ?: throw LineException("unable to generify operator ${expression.operatorSymbol}", expression.line)
    }

    private fun generifyFunctionLang(
        expression: GexExpressionFunction,
        functionEntry: GexFunctionLangEntry
    ): TypeGenerified {
        for (i in expression.args.indices) {
            if (expression.args[i].getTypeGenerifiedNotNull().typeClass != functionEntry.getArgTypeClass(i))
                throw LineException(
                    "type class mismatch when resolving argument ${i+1} of function ${expression.functionSymbol}",
                    expression.line
                )
        }
        return functionEntry.generifier(expression)
            ?: throw LineException("unable to generify function ${expression.functionSymbol}", expression.line)
    }

    private fun generifyFunctionRegular(
        expression: GexExpressionFunction,
        functionEntry: GexFunctionRegularEntry
    ): TypeGenerified {
        for (i in expression.args.indices) {
            val typeGenerified = expression.args[i].getTypeGenerifiedNotNull()
            if (typeGenerified.typeClass != TypeClass.INSTANCE)
                throw LineException("type expression not permitted here", expression.line)
            if (typeGenerified != functionEntry.argTypesGenerified[i])
                throw LineException(
                    "type mismatch when resolving argument ${i+1} of function ${expression.functionSymbol}"
                            + " expected ${functionEntry.argTypesGenerified[i]} but got $typeGenerified",
                    expression.line
                )
        }
        return functionEntry.returnTypeGenerified
    }
}
