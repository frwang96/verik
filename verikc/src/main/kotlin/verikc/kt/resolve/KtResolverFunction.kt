/*
 * Copyright 2020 Francis Wang
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

import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.*
import verikc.kt.symbol.KtSymbolTable

object KtResolverFunction: KtResolverBase() {

    override fun resolvePrimaryType(primaryType: KtPrimaryType, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        primaryType.declarations.forEach { resolveDeclaration(it, primaryType.symbol, symbolTable) }
        resolveConstructorFunction(primaryType.constructorFunction, scopeSymbol, symbolTable)
    }

    override fun resolvePrimaryFunction(
        primaryFunction: KtPrimaryFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        symbolTable.addScope(primaryFunction.symbol, scopeSymbol, primaryFunction.line)
        primaryFunction.parameters.forEach { resolveParameterProperty(it, primaryFunction.symbol, symbolTable) }
        when (primaryFunction.body) {
            is KtFunctionBodyBlock -> {
                primaryFunction.returnTypeSymbol = symbolTable.resolveType(
                    primaryFunction.body.returnTypeIdentifier,
                    scopeSymbol,
                    primaryFunction.line
                )
            }
            is KtFunctionBodyExpression -> {
                throw LineException("resolving functions with expression bodies is not supported", primaryFunction.line)
            }
        }
        symbolTable.addFunction(primaryFunction, scopeSymbol)
    }

    override fun resolveConstructorFunction(
        constructorFunction: KtConstructorFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        symbolTable.addScope(constructorFunction.symbol, scopeSymbol, constructorFunction.line)
        constructorFunction.parameters.forEach {
            resolveParameterProperty(it, constructorFunction.symbol, symbolTable)
        }
        assert(constructorFunction.returnTypeSymbol != null)
        symbolTable.addFunction(constructorFunction, scopeSymbol)
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
