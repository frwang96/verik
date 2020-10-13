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
import verik.core.kt.KtParameterProperty
import verik.core.kt.KtPrimaryType
import verik.core.kt.symbol.KtSymbolTable

object KtResolverTypeContent: KtResolverBase() {

    override fun resolvePrimaryType(primaryType: KtPrimaryType, scope: Symbol, symbolTable: KtSymbolTable) {
        val constructorInvocation = primaryType.constructorInvocation
        constructorInvocation.type = symbolTable.resolveType(constructorInvocation.typeIdentifier, scope, primaryType.line)

        symbolTable.addScope(primaryType.symbol, scope, primaryType.line)
        primaryType.parameters.forEach {
            resolveParameterProperty(it, primaryType.symbol, symbolTable)
        }
        primaryType.constructorFunction.parameters.forEach {
            resolveParameterProperty(it, primaryType.symbol, symbolTable)
        }
        symbolTable.addFunction(primaryType.constructorFunction, scope)
    }

    override fun resolveParameterProperty(parameterProperty: KtParameterProperty, scope: Symbol, symbolTable: KtSymbolTable) {
        parameterProperty.type = symbolTable.resolveType(parameterProperty.typeIdentifier, scope, parameterProperty.line)
        symbolTable.addProperty(parameterProperty, scope)
    }
}