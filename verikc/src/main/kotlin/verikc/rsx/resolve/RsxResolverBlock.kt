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

package verikc.rsx.resolve

import verikc.base.ast.ExpressionClass
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rsx.ast.RsxBlock
import verikc.rsx.ast.RsxStatementDeclaration
import verikc.rsx.ast.RsxStatementExpression
import verikc.rsx.table.RsxSymbolTable

object RsxResolverBlock {

    fun resolve(block: RsxBlock, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        symbolTable.addScope(block.symbol, scopeSymbol, block.line)
        block.lambdaProperties.forEach {
            symbolTable.addProperty(it, block.symbol)
        }
        block.statements.forEach {
            when (it) {
                is RsxStatementDeclaration -> {
                    if (it.property.expression == null)
                        throw LineException("property expression expected", it.line)
                    RsxResolverExpression.resolve(it.property.expression, block.symbol, symbolTable)
                    if (it.property.expression.getExpressionClassNotNull() == ExpressionClass.TYPE)
                        throw LineException("type expression not permitted", it.line)
                    it.property.typeGenerified = it.property.expression.getTypeGenerifiedNotNull()
                    symbolTable.addProperty(it.property, block.symbol)
                }
                is RsxStatementExpression -> {
                    RsxResolverExpression.resolve(it.expression, block.symbol, symbolTable)
                }
            }
        }
    }
}