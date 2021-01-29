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

package verikc.rs.pass

import verikc.base.ast.ExpressionClass
import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable
import verikc.rs.table.RsTypeResolveException

class RsPassFunctionRepeat: RsPassBase() {

    private var throwException = false
    private var isResolved = false

    fun attemptPass(compilationUnit: RsCompilationUnit, throwException: Boolean, symbolTable: RsSymbolTable): Boolean {
        this.throwException = throwException
        isResolved = false
        pass(compilationUnit, symbolTable)
        return isResolved
    }

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        type.functions.forEach { passFunctionWithBlock(it, type.symbol, symbolTable) }
        passFunctionType(type.typeConstructor, TYPE, type.symbol, scopeSymbol, symbolTable)
        type.instanceConstructor?.let {
            passFunctionType(it, VALUE, type.symbol, scopeSymbol, symbolTable)
        }
    }

    override fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        passFunctionWithBlock(function, scopeSymbol, symbolTable)
    }

    private fun passFunctionType(
        function: RsFunction,
        expressionClass: ExpressionClass,
        typeSymbol: Symbol,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        if (function.returnTypeGenerified != null) return

        if (function.block != null) {
            passFunctionWithBlock(function, scopeSymbol, symbolTable)
        } else {
            function.parameterProperties.forEach {
                if (it.expression != null) {
                    if (!attemptPassExpression(it.expression, scopeSymbol, symbolTable)) return
                    if (it.expression.getExpressionClassNotNull() != TYPE)
                        throw LineException("type expression expected", it.expression.line)
                    it.typeGenerified = it.expression.getTypeGenerifiedNotNull()
                    symbolTable.setProperty(it)
                } else throw LineException("parameter property not supported", it.line)
            }
            function.returnTypeGenerified = typeSymbol.toTypeGenerified()
            symbolTable.addFunction(function, expressionClass, scopeSymbol)
        }
    }

    private fun passFunctionWithBlock(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (function.returnTypeGenerified != null) return

        val parameterPropertyTypeSymbols = function.parameterProperties.map {
            if (it.expression != null)
                throw LineException("parameter default arguments not supported", it.line)
            symbolTable.resolveTypeSymbol(it.getTypeIdentifierNotNull(), scopeSymbol, it.line)
        }
        val returnTypeSymbol = symbolTable.resolveTypeSymbol(function.returnTypeIdentifier, scopeSymbol, function.line)

        val typeGenerifiedEntries = getTypeGenerifiedEntries(function, scopeSymbol, symbolTable) ?: return
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
                if (parameterProperty.typeGenerified == null) {
                    parameterProperty.typeGenerified = it.typeGenerified
                }
            }
        }

        function.parameterProperties.forEachIndexed { index, it ->
            if (it.typeGenerified == null) {
                if (!symbolTable.hasTypeParameters(parameterPropertyTypeSymbols[index], it.line)) {
                    it.typeGenerified = parameterPropertyTypeSymbols[index].toTypeGenerified()
                } else throw LineException("type expression expected for function parameter ${it.symbol}", it.line)
            }
            symbolTable.setProperty(it)
        }
        if (function.returnTypeGenerified == null) {
            if (!symbolTable.hasTypeParameters(returnTypeSymbol, function.line)) {
                function.returnTypeGenerified = returnTypeSymbol.toTypeGenerified()
            } else throw LineException("type expression expected for function return value", function.line)

        }
        symbolTable.addFunction(function, VALUE, scopeSymbol)
    }

    private fun getTypeGenerifiedEntries(
        function: RsFunction,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ): List<TypeGenerifiedEntry>? {
        return function.typeFunctionExpressions.map {
            when (it.args.size) {
                1 -> {
                    if (!attemptPassExpression(it.args[0], scopeSymbol, symbolTable)) return null
                    if (it.args[0].getExpressionClassNotNull() != TYPE)
                        throw LineException("type expression expected", it.args[0].line)
                    TypeGenerifiedEntry(it.line, null, it.args[0].getTypeGenerifiedNotNull())
                }
                2 -> {
                    val propertyExpression = it.args[0]
                    val propertyIdentifier = if (propertyExpression is RsExpressionProperty) {
                        if (propertyExpression.receiver == null) propertyExpression.identifier
                        else throw LineException("function parameter expected", propertyExpression.line)
                    } else throw LineException("function parameter expected", propertyExpression.line)

                    if (!attemptPassExpression(it.args[1], scopeSymbol, symbolTable)) return null
                    if (it.args[1].getExpressionClassNotNull() != TYPE)
                        throw LineException("type expression expected", it.args[1].line)
                    TypeGenerifiedEntry(it.line, propertyIdentifier, it.args[1].getTypeGenerifiedNotNull())
                }
                else -> throw LineException("illegal type function", it.line)
            }
        }
    }

    private fun attemptPassExpression(
        expression: RsExpression,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ): Boolean {
        try {
            RsPassExpression.pass(expression, scopeSymbol, symbolTable)
        } catch (exception: RsTypeResolveException) {
            isResolved = false
            if (throwException) throw exception
            else return false
        }
        return true
    }

    private data class TypeGenerifiedEntry(
        val line: Line,
        val propertyIdentifier: String?,
        val typeGenerified: TypeGenerified
    )
}
