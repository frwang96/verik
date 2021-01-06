/*
 * Copyright (c) 2021 Francis Wang
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
import verikc.rs.ast.RsBlock
import verikc.rs.ast.RsStatementDeclaration
import verikc.rs.ast.RsStatementExpression
import verikc.rs.table.RsSymbolTable

object RsResolverBlock {

    fun resolve(block: RsBlock, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(block.symbol, scopeSymbol, block.line)
        block.lambdaProperties.forEach {
            symbolTable.addProperty(it, block.symbol)
        }
        block.statements.forEach {
            when (it) {
                is RsStatementDeclaration -> {
                    RsResolverExpression.resolve(it.primaryProperty.expression, block.symbol, symbolTable)
                    it.primaryProperty.typeSymbol = it.primaryProperty.expression.getTypeSymbolNotNull()
                    symbolTable.addProperty(it.primaryProperty, block.symbol)
                }
                is RsStatementExpression -> {
                    RsResolverExpression.resolve(it.expression, block.symbol, symbolTable)
                }
            }
        }
    }
}