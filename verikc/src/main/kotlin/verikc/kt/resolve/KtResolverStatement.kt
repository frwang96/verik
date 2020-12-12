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

import verikc.base.ast.Symbol
import verikc.kt.ast.KtConstructorFunction
import verikc.kt.ast.KtPrimaryFunction
import verikc.kt.ast.KtPrimaryProperty
import verikc.kt.ast.KtPrimaryType
import verikc.kt.symbol.KtSymbolTable

object KtResolverStatement: KtResolverBase() {

    override fun resolvePrimaryType(primaryType: KtPrimaryType, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        primaryType.parameters.forEach {
            if (it.expression != null) KtResolverExpression.resolve(it.expression, primaryType.symbol, symbolTable)
        }
        resolveConstructorFunction(primaryType.constructorFunction, primaryType.symbol, symbolTable)
        primaryType.declarations.forEach { resolveDeclaration(it, primaryType.symbol, symbolTable) }
    }

    override fun resolvePrimaryFunction(
        primaryFunction: KtPrimaryFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        KtResolverExpression.resolveBlock(primaryFunction.block, primaryFunction.symbol, symbolTable)
    }

    override fun resolveConstructorFunction(
        constructorFunction: KtConstructorFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        constructorFunction.parameters.forEach {
            if (it.expression != null) KtResolverExpression.resolve(
                it.expression,
                constructorFunction.symbol,
                symbolTable
            )
        }
    }

    override fun resolvePrimaryProperty(
        primaryProperty: KtPrimaryProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
        // with expressions have not been resolved
        if (primaryProperty.expression.typeSymbol == null) {
            KtResolverExpression.resolve(primaryProperty.expression, scopeSymbol, symbolTable)
        }
    }
}
