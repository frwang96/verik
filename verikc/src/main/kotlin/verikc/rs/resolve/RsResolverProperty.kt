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
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.rs.ast.RsExpressionFunction
import verikc.rs.ast.RsExpressionOperator
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsResolverProperty: RsResolverBase() {

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addProperty(type, scopeSymbol)
        type.enumProperties.forEach { resolveEnumProperty(it, type.symbol, symbolTable) }
        type.properties.forEach { resolveProperty(it, type.symbol, symbolTable) }
    }

    override fun resolveProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (property.expression == null)
            throw LineException("property expression expected", property.line)
        val expression = property.expression
        if (expression is RsExpressionOperator && expression.operatorSymbol == OPERATOR_WITH) {
            if (expression.receiver != null && expression.receiver is RsExpressionFunction) {
                // we only determine the type of the property here
                property.typeSymbol = symbolTable.resolveType(
                    expression.receiver.identifier,
                    scopeSymbol,
                    expression.receiver.line
                )
            } else throw LineException("could not resolve with expression", expression.line)
        } else {
            RsResolverExpression.resolve(expression, scopeSymbol, symbolTable)
            property.typeSymbol = expression.getTypeSymbolNotNull()
        }
        symbolTable.addProperty(property, scopeSymbol)
    }

    private fun resolveEnumProperty(enumProperty: RsProperty, typeSymbol: Symbol, symbolTable: RsSymbolTable) {
        enumProperty.typeSymbol = typeSymbol
        symbolTable.addProperty(enumProperty, typeSymbol)
        if (enumProperty.expression != null) {
            RsResolverExpression.resolve(enumProperty.expression, typeSymbol, symbolTable)
        }
    }
}
