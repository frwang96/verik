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

package verikc.kt.resolve

import verikc.base.symbol.Symbol
import verikc.kt.ast.KtBlock
import verikc.kt.ast.KtStatementDeclaration
import verikc.kt.ast.KtStatementExpression
import verikc.kt.symbol.KtSymbolTable

object KtResolverBlock {

    fun resolve(block: KtBlock, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        symbolTable.addScope(block.symbol, scopeSymbol, block.line)
        block.lambdaProperties.forEach {
            symbolTable.addProperty(it, block.symbol)
        }
        block.statements.forEach {
            when (it) {
                is KtStatementDeclaration -> {
                    KtResolverExpression.resolve(it.primaryProperty.expression, block.symbol, symbolTable)
                    it.primaryProperty.typeSymbol = it.primaryProperty.expression.getTypeSymbolNotNull()
                    symbolTable.addProperty(it.primaryProperty, block.symbol)
                }
                is KtStatementExpression -> {
                    KtResolverExpression.resolve(it.expression, block.symbol, symbolTable)
                }
            }
        }
    }
}