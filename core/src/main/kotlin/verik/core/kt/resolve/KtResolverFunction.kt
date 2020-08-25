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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtDeclarationFunction
import verik.core.kt.KtDeclarationType
import verik.core.kt.KtFunctionBodyBlock
import verik.core.kt.KtFunctionBodyExpression
import verik.core.kt.symbol.KtSymbolTable

object KtResolverFunction: KtResolverBase() {

    override fun resolveType(type: KtDeclarationType, scope: Symbol, symbolTable: KtSymbolTable) {
        type.declarations.forEach { resolveDeclaration(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: KtDeclarationFunction, scope: Symbol, symbolTable: KtSymbolTable) {
        when (function.body) {
            is KtFunctionBodyBlock -> {
                function.type = symbolTable.resolveType(function.body.typeIdentifier, scope, function.line)
            }
            is KtFunctionBodyExpression -> {
                throw LineException("resolving functions with expression bodies is not supported", function)
            }
        }
    }
}