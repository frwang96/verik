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

import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsExpression
import verikc.rs.ast.RsExpressionProperty
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsType
import verikc.rs.table.RsResolveException
import verikc.rs.table.RsSymbolTable
import verikc.rs.table.RsTypeResult

class RsPassRepeatFunction: RsPassRepeatBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        type.functions.forEach { passFunctionWithBlock(it, type.symbol, symbolTable) }
        passFunctionType(type.typeConstructor, type.symbol, scopeSymbol, symbolTable)
        type.instanceConstructor?.let {
            passFunctionType(it, type.symbol, scopeSymbol, symbolTable)
        }
    }

    override fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        passFunctionWithBlock(function, scopeSymbol, symbolTable)
    }

    private fun passFunctionType(
        function: RsFunction,
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
            symbolTable.setFunction(function)
        }
    }

    private fun passFunctionWithBlock(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (function.parameterProperties.all { it.typeGenerified != null }
            && function.returnTypeGenerified != null
        ) return

        function.parameterProperties.forEach { it.typeGenerified = null }
        function.returnTypeGenerified = null

        val parameterPropertyTypeResults = function.parameterProperties.map {
            if (it.expression != null)
                throw LineException("parameter default arguments not supported", it.line)
            symbolTable.resolveType(it.getTypeIdentifierNotNull(), scopeSymbol, it.line)
        }
        val returnTypeResult = symbolTable.resolveType(function.returnTypeIdentifier, scopeSymbol, function.line)

        function.parameterProperties.forEachIndexed { index, it ->
            val (typeGenerified, success) = attemptGetTypeGenerified(parameterPropertyTypeResults[index], it.line)
            if (!success) return
            it.typeGenerified = typeGenerified
        }
        val (typeGenerified, success) = attemptGetTypeGenerified(returnTypeResult, function.line)
        if (!success) return
        function.returnTypeGenerified = typeGenerified

        val typeGenerifiedEntries = getTypeGenerifiedEntries(function, scopeSymbol, symbolTable) ?: return
        typeGenerifiedEntries.forEach {
            if (it.propertyIdentifier == null) {
                if (returnTypeResult.typeSymbol != it.typeGenerified.typeSymbol)
                    throw LineException("type mismatch for function return type", it.line)
                if (function.returnTypeGenerified != null)
                    throw LineException("function return value has already been assigned a type", it.line)
                function.returnTypeGenerified = it.typeGenerified
            } else {
                val index = function.parameterProperties.indexOfFirst { parameterProperty ->
                    parameterProperty.identifier == it.propertyIdentifier
                }
                if (index == -1)
                     throw LineException("function parameter expected", it.line)
                val parameterProperty = function.parameterProperties[index]
                if (parameterPropertyTypeResults[index].typeSymbol != it.typeGenerified.typeSymbol)
                    throw LineException("type mismatch for function parameter ${parameterProperty.symbol}", it.line)
                if (parameterProperty.typeGenerified != null)
                    throw LineException(
                        "function parameter ${parameterProperty.symbol} has already been assigned a type",
                        it.line
                    )
                parameterProperty.typeGenerified = it.typeGenerified
            }
        }

        function.parameterProperties.forEach {
            if (it.typeGenerified == null)
                throw LineException("type function expected for function parameter ${it.symbol}", it.line)
            symbolTable.setProperty(it)
        }
        if (function.returnTypeGenerified == null)
            throw LineException("type function expected for function return value", function.line)
        symbolTable.setFunction(function)
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
        } catch (exception: RsResolveException) {
            isResolved = false
            if (throwException) throw exception
            return false
        }
        return true
    }

    private fun attemptGetTypeGenerified(typeResult: RsTypeResult, line: Line): Pair<TypeGenerified?, Boolean> {
        return if (typeResult.typeGenerified != null) {
            Pair(typeResult.typeGenerified, true)
        } else {
            if (typeResult.isTypeAlias) {
                isResolved = false
                if (throwException)
                    throw LineException("could not resolve type of type alias ${typeResult.symbol}", line)
                Pair(null, false)
            } else {
                Pair(null, true)
            }
        }
    }

    private data class TypeGenerifiedEntry(
        val line: Line,
        val propertyIdentifier: String?,
        val typeGenerified: TypeGenerified
    )
}
