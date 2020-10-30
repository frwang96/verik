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

package verik.core.kt.resolve

import verik.core.base.Symbol
import verik.core.kt.ast.*
import verik.core.kt.symbol.KtSymbolTable

object KtResolverStatement: KtResolverBase() {

    override fun resolvePrimaryType(primaryType: KtPrimaryType, scope: Symbol, symbolTable: KtSymbolTable) {
        primaryType.parameters.forEach {
            if (it.expression != null) KtResolverExpression.resolve(it.expression, primaryType.symbol, symbolTable)
        }
        resolveConstructorFunction(primaryType.constructorFunction, primaryType.symbol, symbolTable)
        primaryType.declarations.forEach { resolveDeclaration(it, primaryType.symbol, symbolTable) }
    }

    override fun resolvePrimaryFunction(primaryFunction: KtPrimaryFunction, scope: Symbol, symbolTable: KtSymbolTable) {
        when (primaryFunction.body) {
            is KtFunctionBodyBlock -> {
                KtResolverExpression.resolveBlock(primaryFunction.body.block, primaryFunction.symbol, symbolTable)
            }
            is KtFunctionBodyExpression -> {
                KtResolverExpression.resolve(primaryFunction.body.expression, primaryFunction.symbol, symbolTable)
            }
        }
    }

    override fun resolveConstructorFunction(constructorFunction: KtConstructorFunction, scope: Symbol, symbolTable: KtSymbolTable) {
        constructorFunction.parameters.forEach {
            if (it.expression != null) KtResolverExpression.resolve(it.expression, constructorFunction.symbol, symbolTable)
        }
    }

    override fun resolvePrimaryProperty(primaryProperty: KtPrimaryProperty, scope: Symbol, symbolTable: KtSymbolTable) {
        // with expressions have not been resolved
        if (primaryProperty.expression.type == null) {
            KtResolverExpression.resolve(primaryProperty.expression, scope, symbolTable)
        }
    }
}