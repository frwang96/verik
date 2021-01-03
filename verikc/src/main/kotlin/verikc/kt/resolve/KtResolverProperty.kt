/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.base.symbol.Symbol
import verikc.kt.ast.*
import verikc.kt.symbol.KtSymbolTable
import verikc.lang.LangSymbol.OPERATOR_WITH

object KtResolverProperty: KtResolverBase() {

    override fun resolveType(type: KtType, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        symbolTable.addProperty(type, scopeSymbol)
        type.declarations.forEach {
            when (it) {
                is KtPrimaryProperty -> resolvePrimaryProperty(it, type.symbol, symbolTable)
                is KtEnumProperty -> resolveEnumProperty(it, type.symbol, symbolTable)
            }
        }
    }

    override fun resolvePrimaryProperty(
        primaryProperty: KtPrimaryProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable,
    ) {
        val expression = primaryProperty.expression
        if (expression is KtExpressionOperator && expression.operatorSymbol == OPERATOR_WITH) {
            if (expression.receiver != null && expression.receiver is KtExpressionFunction) {
                // we only determine the type of the property here
                primaryProperty.typeSymbol = symbolTable.resolveType(
                    expression.receiver.identifier,
                    scopeSymbol,
                    expression.receiver.line
                )
            } else throw LineException("could not resolve with expression", expression.line)
        } else {
            KtResolverExpression.resolve(expression, scopeSymbol, symbolTable)
            primaryProperty.typeSymbol = expression.getTypeSymbolNotNull()
        }
        symbolTable.addProperty(primaryProperty, scopeSymbol)
    }

    private fun resolveEnumProperty(enumProperty: KtEnumProperty, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        symbolTable.addProperty(enumProperty, scopeSymbol)
        if (enumProperty.arg != null) {
            KtResolverExpression.resolve(enumProperty.arg, scopeSymbol, symbolTable)
        }
    }
}
