/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs.resolve

import verikc.base.ast.ExpressionClass
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsResolverPassFunction: RsResolverPassBase() {

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        resolveFunction(type.typeConstructorFunction, scopeSymbol, symbolTable)
        type.functions.forEach { resolveFunction(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(function.symbol, scopeSymbol, function.line)

        val parameterPropertyTypeSymbols = function.parameterProperties.map {
            if (it.typeIdentifier == null)
                throw LineException("parameter property type identifier expected", it.line)
            symbolTable.resolveTypeSymbol(it.typeIdentifier, scopeSymbol, it.line)
        }
        val returnTypeSymbol = symbolTable.resolveTypeSymbol(function.returnTypeIdentifier, scopeSymbol, function.line)

        val typeGenerifiedEntries = getTypeGenerifiedEntries(function.block, scopeSymbol, symbolTable)
        typeGenerifiedEntries.forEach {
            if (it.propertyIdentifier == null) {
                if (returnTypeSymbol != it.typeGenerified.typeSymbol)
                    throw LineException("type mismatch for function return type", it.line)
                function.returnTypeGenerified = it.typeGenerified
            } else {
                val index = function.parameterProperties.indexOfFirst { parameterProperty ->
                    parameterProperty.identifier == it.propertyIdentifier
                }
                if (index == -1)
                     throw LineException("function parameter expected", it.line)
                val parameterProperty = function.parameterProperties[index]
                if (parameterPropertyTypeSymbols[index] != it.typeGenerified.typeSymbol)
                    throw LineException("type mismatch for function parameter ${parameterProperty.symbol}", it.line)
                parameterProperty.typeGenerified = it.typeGenerified
            }
        }

        function.parameterProperties.forEachIndexed { index, it ->
            if (it.typeGenerified == null) {
                if (!symbolTable.hasTypeParameters(parameterPropertyTypeSymbols[index], it.line)) {
                    it.typeGenerified = parameterPropertyTypeSymbols[index].toTypeGenerified()
                } else throw LineException("type expression expected for function parameter ${it.symbol}", it.line)
            }
            symbolTable.addProperty(it, scopeSymbol)
        }
        if (function.returnTypeGenerified == null) {
            if (!symbolTable.hasTypeParameters(returnTypeSymbol, function.line)) {
                function.returnTypeGenerified = returnTypeSymbol.toTypeGenerified()
            } else throw LineException("type expression expected for function return value", function.line)

        }
        symbolTable.addFunction(function, scopeSymbol)
    }

    private fun getTypeGenerifiedEntries(
        block: RsBlock,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ): List<TypeGenerifiedEntry> {
        val typeGenerifiedEntries = ArrayList<TypeGenerifiedEntry>()
        for (statement in block.statements) {
            if (statement is RsStatementExpression && statement.expression is RsExpressionFunction) {
                val expression = statement.expression
                if (expression.identifier == "type") {
                    when (expression.args.size) {
                        1 -> {
                            RsResolverExpression.resolve(expression.args[0], scopeSymbol, symbolTable)
                            if (expression.args[0].getExpressionClassNotNull() != ExpressionClass.TYPE)
                                throw LineException("type expression expected", expression.args[0].line)
                            typeGenerifiedEntries.add(
                                TypeGenerifiedEntry(
                                    expression.line,
                                    null,
                                    expression.args[0].getTypeGenerifiedNotNull()
                                )
                            )
                        }
                        2 -> {
                            val propertyExpression = expression.args[0]
                            val propertyIdentifier = if (propertyExpression is RsExpressionProperty) {
                                if (propertyExpression.receiver == null) propertyExpression.identifier
                                else throw LineException("function parameter expected", propertyExpression.line)
                            } else throw LineException("function parameter expected", propertyExpression.line)

                            RsResolverExpression.resolve(expression.args[1], scopeSymbol, symbolTable)
                            if (expression.args[1].getExpressionClassNotNull() != ExpressionClass.TYPE)
                                throw LineException("type expression expected", expression.args[1].line)
                            typeGenerifiedEntries.add(
                                TypeGenerifiedEntry(
                                    expression.line,
                                    propertyIdentifier,
                                    expression.args[1].getTypeGenerifiedNotNull()
                                )
                            )
                        }
                    }
                }
            }
        }
        return typeGenerifiedEntries
    }

    private data class TypeGenerifiedEntry(
        val line: Line,
        val propertyIdentifier: String?,
        val typeGenerified: TypeGenerified
    )
}
