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
import verik.core.kt.*
import verik.core.kt.symbol.KtSymbolTable

object KtResolverStatement: KtResolverBase() {

    override fun resolveType(type: KtDeclarationType, parent: Symbol, symbolTable: KtSymbolTable) {
        type.declarations.forEach { resolveDeclaration(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: KtDeclarationFunction, parent: Symbol, symbolTable: KtSymbolTable) {
        when (function.body) {
            is KtFunctionBodyBlock -> {
                function.body.block.statements.forEach {
                    if (it is KtStatementExpression) {
                        KtResolverExpression.resolve(it.expression, function.symbol, symbolTable)
                    }
                }
            }
            is KtFunctionBodyExpression -> {
                KtResolverExpression.resolve(function.body.expression, function.symbol, symbolTable)
            }
        }
    }
}