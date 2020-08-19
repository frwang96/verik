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

import verik.core.kt.KtDeclarationBaseProperty
import verik.core.kt.KtDeclarationFunction
import verik.core.kt.KtDeclarationType
import verik.core.kt.symbol.KtSymbolTable
import verik.core.base.LineException
import verik.core.base.Symbol

object KtResolverProperty: KtResolverBase() {

    override fun resolveType(type: KtDeclarationType, parent: Symbol, symbolTable: KtSymbolTable) {
        type.parameters.forEach { resolveDeclaration(it, type.symbol, symbolTable) }
        type.declarations.forEach { resolveDeclaration(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: KtDeclarationFunction, parent: Symbol, symbolTable: KtSymbolTable) {
        function.parameters.forEach { resolveDeclaration(it, function.symbol, symbolTable) }
    }

    override fun resolveBaseProperty(baseProperty: KtDeclarationBaseProperty, parent: Symbol, symbolTable: KtSymbolTable) {
        KtResolverExpression.resolveExpression(baseProperty.expression, parent, symbolTable)
        baseProperty.type = baseProperty.expression.type
                ?: throw LineException("could not resolve expression", baseProperty.expression)
    }
}