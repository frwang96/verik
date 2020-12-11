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

    override fun resolvePrimaryType(primaryType: KtPrimaryType, scope: Symbol, symbolTable: KtSymbolTable) {
        primaryType.declarations.forEach { resolveDeclaration(it, primaryType.symbol, symbolTable) }
        resolveConstructorFunction(primaryType.constructorFunction, scope, symbolTable)
    }

    override fun resolvePrimaryFunction(
        primaryFunction: KtPrimaryFunction,
        scope: Symbol,
        symbolTable: KtSymbolTable
    ) {
        symbolTable.addScope(primaryFunction.symbol, scope, primaryFunction.line)
        primaryFunction.parameters.forEach { resolveParameterProperty(it, primaryFunction.symbol, symbolTable) }
        when (primaryFunction.body) {
            is KtFunctionBodyBlock -> {
                primaryFunction.returnType = symbolTable.resolveType(
                    primaryFunction.body.returnTypeIdentifier,
                    scope,
                    primaryFunction.line
                )
            }
            is KtFunctionBodyExpression -> {
                throw LineException("resolving functions with expression bodies is not supported", primaryFunction.line)
            }
        }
        symbolTable.addFunction(primaryFunction, scope)
    }

    override fun resolveConstructorFunction(
        constructorFunction: KtConstructorFunction,
        scope: Symbol,
        symbolTable: KtSymbolTable
    ) {
        symbolTable.addScope(constructorFunction.symbol, scope, constructorFunction.line)
        constructorFunction.parameters.forEach { resolveParameterProperty(it, constructorFunction.symbol, symbolTable) }
        assert(constructorFunction.returnType != null)
        symbolTable.addFunction(constructorFunction, scope)
    }

    override fun resolveParameterProperty(
        parameterProperty: KtParameterProperty,
        scope: Symbol,
        symbolTable: KtSymbolTable
    ) {
        parameterProperty.type =
            symbolTable.resolveType(parameterProperty.typeIdentifier, scope, parameterProperty.line)
        symbolTable.addProperty(parameterProperty, scope)
    }
}
