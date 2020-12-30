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

package verikc.kt.resolve

import verikc.base.symbol.Symbol
import verikc.kt.ast.KtFunction
import verikc.kt.ast.KtFunctionType
import verikc.kt.ast.KtParameterProperty
import verikc.kt.ast.KtType
import verikc.kt.symbol.KtSymbolTable

object KtResolverFunction: KtResolverBase() {

    override fun resolveType(type: KtType, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        type.declarations.forEach {
            if (it is KtFunction
                && it.type in listOf(KtFunctionType.TYPE_CONSTRUCTOR, KtFunctionType.INSTANCE_CONSTRUCTOR)
            ) {
                resolveFunction(it, scopeSymbol, symbolTable)
            } else {
                resolveDeclaration(it, type.symbol, symbolTable)
            }
        }
    }

    override fun resolveFunction(
        function: KtFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        symbolTable.addScope(function.symbol, scopeSymbol, function.line)
        function.parameters.forEach { resolveParameterProperty(it, function.symbol, symbolTable) }
        function.returnTypeSymbol = function.returnTypeSymbol
            ?: symbolTable.resolveType(
                function.returnTypeIdentifier,
                scopeSymbol,
                function.line
            )
        symbolTable.addFunction(function, scopeSymbol)
    }

    override fun resolveParameterProperty(
        parameterProperty: KtParameterProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        parameterProperty.typeSymbol =
            symbolTable.resolveType(parameterProperty.typeIdentifier, scopeSymbol, parameterProperty.line)
        symbolTable.addProperty(parameterProperty, scopeSymbol)
    }
}
