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

import verikc.base.symbol.Symbol
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsResolverBulk: RsResolverBase() {

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        type.parameterProperties.forEach {
            if (it.expression != null) RsResolverExpression.resolve(it.expression, type.symbol, symbolTable)
        }
        type.functions.forEach { resolveFunction(it, type.symbol, symbolTable) }
        type.properties.forEach { resolveProperty(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        function.parameterProperties.forEach {
            if (it.expression != null) RsResolverExpression.resolve(it.expression, function.symbol, symbolTable)
        }
        RsResolverBlock.resolve(function.block, function.symbol, symbolTable)
    }

    override fun resolveProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        // with expressions have not been resolved
        if (property.expression != null) {
            if (property.expression.typeSymbol == null) {
                RsResolverExpression.resolve(property.expression, scopeSymbol, symbolTable)
            }
        }
    }
}
