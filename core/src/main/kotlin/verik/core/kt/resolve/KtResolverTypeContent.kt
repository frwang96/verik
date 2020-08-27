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
import verik.core.kt.KtDeclarationParameter
import verik.core.kt.KtDeclarationType
import verik.core.kt.symbol.KtSymbolTable

object KtResolverTypeContent: KtResolverBase() {

    override fun resolveType(type: KtDeclarationType, scope: Symbol, symbolTable: KtSymbolTable) {
        val constructorInvocation = type.constructorInvocation
        constructorInvocation.type = symbolTable.resolveType(constructorInvocation.typeIdentifier, scope, type.line)

        symbolTable.addScope(type.symbol, scope, type.line)
        type.parameters.forEach { resolveParameter(it, type.symbol, symbolTable) }
        symbolTable.addFunction(type, scope)
    }

    override fun resolveParameter(parameter: KtDeclarationParameter, scope: Symbol, symbolTable: KtSymbolTable) {
        parameter.type = symbolTable.resolveType(parameter.typeIdentifier, scope, parameter.line)
        symbolTable.addProperty(parameter, scope)
    }
}