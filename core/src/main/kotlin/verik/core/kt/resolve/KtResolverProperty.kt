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
import verik.core.kt.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.LangSymbol.OPERATOR_WITH

object KtResolverProperty: KtResolverBase() {

    override fun resolvePrimaryType(primaryType: KtPrimaryType, scope: Symbol, symbolTable: KtSymbolTable) {
        primaryType.parameters.forEach { resolveDeclaration(it, primaryType.symbol, symbolTable) }
        primaryType.declarations.forEach { resolveDeclaration(it, primaryType.symbol, symbolTable) }
    }

    override fun resolvePrimaryFunction(
            primaryFunction: KtPrimaryFunction,
            scope: Symbol,
            symbolTable: KtSymbolTable,
    ) {
        primaryFunction.parameters.forEach { resolveDeclaration(it, primaryFunction.symbol, symbolTable) }
    }

    override fun resolvePrimaryProperty(
            primaryProperty: KtPrimaryProperty,
            scope: Symbol,
            symbolTable: KtSymbolTable,
    ) {
        val expression = primaryProperty.expression
        if (expression is KtExpressionOperator && expression.operator == OPERATOR_WITH) {
            if (expression.receiver != null && expression.receiver is KtExpressionFunction) {
                primaryProperty.type = symbolTable.resolveType(
                        expression.receiver.identifier,
                        scope,
                        expression.receiver.line
                )
            } else throw LineException("could not resolve with expression", expression)
        } else {
            KtResolverExpression.resolve(expression, scope, symbolTable)
            primaryProperty.type = expression.type!!
        }
        symbolTable.addProperty(primaryProperty, scope)
    }
}