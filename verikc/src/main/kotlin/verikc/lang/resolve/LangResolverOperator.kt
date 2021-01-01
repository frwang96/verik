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

package verikc.lang.resolve

import verikc.base.symbol.Symbol
import verikc.kt.ast.KtStatementExpression
import verikc.kt.symbol.KtOperatorResolverRequest
import verikc.lang.LangSymbol.TYPE_ANY
import kotlin.math.min

object LangResolverOperator {

    fun resolveIfElse(request: KtOperatorResolverRequest): Symbol {
        val ifBlock = request.expression.blocks[0]
        val elseBlock = request.expression.blocks[1]
        if (ifBlock.statements.isEmpty() || elseBlock.statements.isEmpty()) return TYPE_ANY

        val ifStatement = ifBlock.statements.last()
        val elseStatement = ifBlock.statements.last()
        if (ifStatement !is KtStatementExpression || elseStatement !is KtStatementExpression) return TYPE_ANY

        val ifExpression = ifStatement.expression
        val elseExpression = elseStatement.expression

        val ifExpressionParentTypeSymbols =
            request.symbolTable.getParentTypeSymbols(ifExpression.getTypeSymbolNotNull(), request.expression.line)
        val elseExpressionParentTypeSymbols =
            request.symbolTable.getParentTypeSymbols(elseExpression.getTypeSymbolNotNull(), request.expression.line)
        return findClosestCommonParent(ifExpressionParentTypeSymbols, elseExpressionParentTypeSymbols)
    }

    private fun findClosestCommonParent(
        leftParentTypeSymbols: List<Symbol>,
        rightParentTypeSymbols: List<Symbol>
    ): Symbol {
        val leftSize = leftParentTypeSymbols.size
        val rightSize = rightParentTypeSymbols.size
        var typeSymbol = TYPE_ANY
        for (i in 0 until min(leftSize, rightSize)) {
            if (leftParentTypeSymbols[leftSize - i - 1] == rightParentTypeSymbols[rightSize - i - 1]) {
                typeSymbol = leftParentTypeSymbols[leftSize - i - 1]
            } else {
                return typeSymbol
            }
        }
        return typeSymbol
    }
}