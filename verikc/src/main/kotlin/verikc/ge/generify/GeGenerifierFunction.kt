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

package verikc.ge.generify

import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.ge.ast.*
import verikc.ge.table.GeSymbolTable
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY_ANY
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT

object GeGenerifierFunction: GeGenerifierBase() {

    override fun generifyType(type: GeType, symbolTable: GeSymbolTable) {
        symbolTable.addProperty(type)
        generifyFunction(type.typeConstructorFunction, symbolTable)
        type.functions.forEach { generifyFunction(it, symbolTable) }
    }

    override fun generifyFunction(function: GeFunction, symbolTable: GeSymbolTable) {
        val typeGenerifiedEntries = getTypeGenerifiedEntries(function.block, symbolTable)
        typeGenerifiedEntries.forEach {
            if (it.propertySymbol == Symbol.NULL) {
                if (function.returnTypeSymbol != it.typeGenerified.typeSymbol)
                    throw LineException("type mismatch for function return type", it.line)
                function.returnTypeGenerified = it.typeGenerified
            } else {
                val parameterProperty = function.parameterProperties.find { parameterProperty ->
                    parameterProperty.symbol == it.propertySymbol
                }
                    ?: throw LineException("function parameter expected", it.line)
                if (parameterProperty.typeSymbol != it.typeGenerified.typeSymbol)
                    throw LineException("type mismatch for function parameter", it.line)
                parameterProperty.typeGenerified = it.typeGenerified
            }
        }

        // TODO more general way of setting default generified type
        function.parameterProperties.forEach {
            if (it.typeGenerified == null) {
                if (it.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT))
                    throw LineException("type expression expected for function parameter", it.line)
                it.typeGenerified = it.typeSymbol.toTypeGenerified()
            }
            symbolTable.addProperty(it)
        }
        if (function.returnTypeGenerified == null) {
            if (function.returnTypeSymbol in listOf(TYPE_UBIT, TYPE_SBIT))
                throw LineException("type expression expected for function return value", function.line)
            function.returnTypeGenerified = function.returnTypeSymbol.toTypeGenerified()
        }
        symbolTable.addFunction(function)
    }

    private fun getTypeGenerifiedEntries(block: GeBlock, symbolTable: GeSymbolTable): List<TypeGenerifiedEntry> {
        val typeGenerifiedEntries = ArrayList<TypeGenerifiedEntry>()
        for (statement in block.statements) {
            if (statement is GeStatementExpression && statement.expression is GeExpressionFunction) {
                val expression = statement.expression
                if (expression.functionSymbol == FUNCTION_TYPE_ANY_ANY) {
                    val propertyExpression = expression.args[0]
                    val propertySymbol = if (propertyExpression is GeExpressionProperty) {
                        if (propertyExpression.receiver == null) propertyExpression.propertySymbol
                        else throw LineException("function parameter expected", propertyExpression.line)
                    } else throw LineException("function parameter expected", propertyExpression.line)

                    GeGenerifierExpression.generify(expression.args[1], symbolTable)
                    if (expression.args[1].getExpressionClassNotNull() != TYPE)
                        throw LineException("type expression expected", expression.args[1].line)
                    typeGenerifiedEntries.add(
                        TypeGenerifiedEntry(
                            expression.line,
                            propertySymbol,
                            expression.args[1].getTypeGenerifiedNotNull()
                        )
                    )
                }
                if (expression.functionSymbol == FUNCTION_TYPE_ANY) {
                    GeGenerifierExpression.generify(expression.args[0], symbolTable)
                    if (expression.args[0].getExpressionClassNotNull() != TYPE)
                        throw LineException("type expression expected", expression.args[0].line)
                    typeGenerifiedEntries.add(
                        TypeGenerifiedEntry(
                            expression.line,
                            Symbol.NULL,
                            expression.args[0].getTypeGenerifiedNotNull()
                        )
                    )
                }
            }
        }
        return typeGenerifiedEntries
    }

    private data class TypeGenerifiedEntry(
        val line: Line,
        val propertySymbol: Symbol,
        val typeGenerified: TypeGenerified
    )
}
