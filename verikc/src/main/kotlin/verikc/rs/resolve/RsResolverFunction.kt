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

package verikc.rs.resolve

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsResolverFunction: RsResolverBase() {

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        resolveFunction(type.typeConstructorFunction, scopeSymbol, symbolTable)
        type.functions.forEach { resolveFunction(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(function.symbol, scopeSymbol, function.line)
        function.parameterProperties.forEach {
            resolveParameterProperty(it, function.symbol, symbolTable)
        }
        function.returnTypeSymbol = function.returnTypeSymbol
            ?: symbolTable.resolveType(
                function.returnTypeIdentifier,
                scopeSymbol,
                function.line
            )
        symbolTable.addFunction(function, scopeSymbol)
    }

    private fun resolveParameterProperty(
        parameterProperty: RsProperty,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        if (parameterProperty.typeIdentifier == null)
            throw LineException("parameter property type identifier expected", parameterProperty.line)
        parameterProperty.typeSymbol =
            symbolTable.resolveType(parameterProperty.typeIdentifier, scopeSymbol, parameterProperty.line)
        symbolTable.addProperty(parameterProperty, scopeSymbol)
    }
}
