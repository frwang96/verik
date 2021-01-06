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

package verikc.rs.resolve

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.rs.table.RsSymbolTable

object RsResolverProperty: RsResolverBase() {

    override fun resolveType(type: KtType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addProperty(type, scopeSymbol)
        type.properties.forEach {
            when (it) {
                is KtPrimaryProperty -> resolvePrimaryProperty(it, type.symbol, symbolTable)
                is KtEnumProperty -> resolveEnumProperty(it, type.symbol, symbolTable)
                else -> {}
            }
        }
    }

    override fun resolvePrimaryProperty(
        primaryProperty: KtPrimaryProperty,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable,
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
            RsResolverExpression.resolve(expression, scopeSymbol, symbolTable)
            primaryProperty.typeSymbol = expression.getTypeSymbolNotNull()
        }
        symbolTable.addProperty(primaryProperty, scopeSymbol)
    }

    private fun resolveEnumProperty(enumProperty: KtEnumProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addProperty(enumProperty, scopeSymbol)
        if (enumProperty.arg != null) {
            RsResolverExpression.resolve(enumProperty.arg, scopeSymbol, symbolTable)
        }
    }
}
