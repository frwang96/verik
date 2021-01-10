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

import verikc.base.ast.ExpressionClass
import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.Line
import verikc.base.ast.LineException
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
                function.argExpressionClasses,
                function.isVararg,
                function.returnExpressionClass,
                function.generifier
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = GeOperatorEntry(
                operator.symbol,
                operator.returnExpressionClass,
                operator.generifier
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addFunction(function: GeFunction) {
        val functionEntry = GeFunctionRegularEntry(
            function.symbol,
            function.parameterProperties.map { it.getTypeGenerifiedNotNull() },
            function.getReturnTypeGenerifiedNotNull()
        )
        functionEntryMap.add(functionEntry, function.line)
    }

    fun addProperty(property: GeProperty) {
        val propertyEntry = GePropertyEntry(property.symbol, property.getTypeGenerifiedNotNull(), VALUE)
        propertyEntryMap.add(propertyEntry, property.line)
    }

    fun addProperty(type: GeType) {
        val propertyEntry = GePropertyEntry(type.symbol, type.symbol.toTypeGenerified(), TYPE)
        propertyEntryMap.add(propertyEntry, type.line)
    }

    fun generifyProperty(expression: GeExpressionProperty): GeGenerifierResult {
        val propertyEntry = propertyEntryMap.get(expression.propertySymbol, expression.line)
        return GeGenerifierResult(propertyEntry.typeGenerified, propertyEntry.expressionClass)
    }

    fun generifyFunction(expression: GeExpressionFunction): GeGenerifierResult {
        return when (val functionEntry = functionEntryMap.get(expression.functionSymbol, expression.line)) {
            is GeFunctionLangEntry -> generifyFunctionLang(expression, functionEntry)
            is GeFunctionRegularEntry -> generifyFunctionRegular(expression, functionEntry)
        }
    }

    fun generifyOperator(expression: GeExpressionOperator): GeGenerifierResult {
        val operatorEntry = operatorEntryMap.get(expression.operatorSymbol, expression.line)
        val typeGenerified = operatorEntry.generifier(expression)
            ?: throw LineException("unable to generify operator ${expression.operatorSymbol}", expression.line)
        return GeGenerifierResult(typeGenerified, operatorEntry.returnExpressionClass)
    }

    private fun generifyFunctionLang(
        expression: GeExpressionFunction,
        functionEntry: GeFunctionLangEntry
    ): GeGenerifierResult {
        expression.receiver?.let {
            compareExpressionClass(
                VALUE,
                it.getExpressionClassNotNull(),
                "receiver of function ${expression.functionSymbol}",
                expression.line
            )
        }
        for (i in expression.args.indices) {
            compareExpressionClass(
                functionEntry.getArgExpressionClass(i),
                expression.args[i].getExpressionClassNotNull(),
                "argument ${i + 1} of function ${expression.functionSymbol}",
                expression.line
            )
        }
        val typeGenerified = functionEntry.generifier(expression)
            ?: throw LineException("unable to generify function ${expression.functionSymbol}", expression.line)
        return GeGenerifierResult(typeGenerified, functionEntry.returnExpressionClass)
    }

    private fun generifyFunctionRegular(
        expression: GeExpressionFunction,
        functionEntry: GeFunctionRegularEntry
    ): GeGenerifierResult {
        expression.receiver?.let {
            compareExpressionClass(
                VALUE,
                it.getExpressionClassNotNull(),
                "receiver of function ${expression.functionSymbol}",
                expression.line
            )
        }
        for (i in expression.args.indices) {
            compareExpressionClass(
                VALUE,
                expression.args[i].getExpressionClassNotNull(),
                "argument ${i + 1} of function ${expression.functionSymbol}",
                expression.line
            )
            compareTypeGenerified(
                functionEntry.argTypesGenerified[i],
                expression.args[i].getTypeGenerifiedNotNull(),
                "argument ${i + 1} of function ${expression.functionSymbol}",
                expression.line
            )
        }
        return GeGenerifierResult(functionEntry.returnTypeGenerified, VALUE)
    }

    private fun compareExpressionClass(
        expectedExpressionClass: ExpressionClass,
        actualExpressionClass: ExpressionClass,
        string: String,
        line: Line
    ) {
        if (expectedExpressionClass != actualExpressionClass) {
            when (expectedExpressionClass) {
                TYPE -> throw LineException("type expression expected in $string", line)
                VALUE -> throw LineException("type expression not permitted in $string", line)
            }
        }
    }

    private fun compareTypeGenerified(
        expectedTypeGenerified: TypeGenerified,
        actualTypeGenerified: TypeGenerified,
        string: String,
        line: Line
    ) {
        if (expectedTypeGenerified != actualTypeGenerified) {
            throw LineException(
                "type mismatch when in $string expected $expectedTypeGenerified but got $actualTypeGenerified",
                line
            )
        }
    }
}
