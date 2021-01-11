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

package verikc.rsx.resolve

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rsx.ast.RsxFunction
import verikc.rsx.ast.RsxProperty
import verikc.rsx.ast.RsxType
import verikc.rsx.table.RsxSymbolTable

object RsxResolverPassFunction: RsxResolverPassBase() {

    override fun resolveType(type: RsxType, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        resolveFunction(type.typeConstructorFunction, scopeSymbol, symbolTable)
        type.functions.forEach { resolveFunction(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: RsxFunction, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        symbolTable.addScope(function.symbol, scopeSymbol, function.line)
        function.parameterProperties.forEach {
            resolveParameterProperty(it, function.symbol, symbolTable)
        }
        // TODO resolve generified types
        function.returnTypeGenerified = function.returnTypeGenerified
            ?: symbolTable.resolveTypeSymbol(
                function.returnTypeIdentifier,
                scopeSymbol,
                function.line
            ).toTypeGenerified()
        symbolTable.addFunction(function, scopeSymbol)
    }

    private fun resolveParameterProperty(
        parameterProperty: RsxProperty,
        scopeSymbol: Symbol,
        symbolTable: RsxSymbolTable
    ) {
        if (parameterProperty.typeIdentifier == null)
            throw LineException("parameter property type identifier expected", parameterProperty.line)
        parameterProperty.typeGenerified = symbolTable
            .resolveTypeSymbol(parameterProperty.typeIdentifier, scopeSymbol, parameterProperty.line)
            .toTypeGenerified()
        symbolTable.addProperty(parameterProperty, scopeSymbol)
    }
}
