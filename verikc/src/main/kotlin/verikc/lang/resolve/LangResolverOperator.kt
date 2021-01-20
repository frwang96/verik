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

import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rs.ast.RsStatementExpression
import verikc.rs.table.RsOperatorResolverRequest
import kotlin.math.min

object LangResolverOperator {

    fun resolveIfElse(request: RsOperatorResolverRequest): TypeGenerified {
        val ifStatement = request.expression.blocks[0].statements.lastOrNull()
        val elseStatement = request.expression.blocks[1].statements.lastOrNull()
        val ifExpression = if (ifStatement is RsStatementExpression) ifStatement.expression else null
        val elseExpression = if (elseStatement is RsStatementExpression) elseStatement.expression else null

        if (ifExpression == null || elseExpression == null) return TYPE_ANY.toTypeGenerified()

        val ifExpressionParentTypeSymbols = request.symbolTable.getParentTypeSymbols(
            ifExpression.getTypeGenerifiedNotNull().typeSymbol,
            request.expression.line
        )
        val elseExpressionParentTypeSymbols = request.symbolTable.getParentTypeSymbols(
            elseExpression.getTypeGenerifiedNotNull().typeSymbol,
            request.expression.line
        )

        return when (val typeSymbol = findClosestCommonParent(
                ifExpressionParentTypeSymbols,
                elseExpressionParentTypeSymbols
            )
        ) {
            TYPE_UBIT, TYPE_SBIT -> {
                LangResolverCommon.inferWidthIfBit(ifExpression, elseExpression)
                LangResolverCommon.matchTypes(ifExpression, elseExpression)
                ifExpression.typeGenerified!!
            }
            else -> typeSymbol.toTypeGenerified()
        }
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