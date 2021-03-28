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

object LangResolverOperator {

    fun resolveIfElseWhen(request: RsOperatorResolverRequest): TypeGenerified {
        val statements = request.expression.blocks.map { it.statements.lastOrNull() }
        val expressions = statements.map {
            if (it is RsStatementExpression) it.expression else return TYPE_ANY.toTypeGenerified()
        }

        val parentTypeSymbols = expressions.map {
            request.symbolTable.getParentTypeSymbols(it.getTypeGenerifiedNotNull().typeSymbol, request.expression.line)
        }

        return when (val typeSymbol = findClosestCommonParent(parentTypeSymbols)) {
            TYPE_UBIT, TYPE_SBIT -> {
                val indexReference = expressions.indexOfFirst { LangResolverCommon.bitToWidth(it) != 0 }
                if (indexReference != -1) {
                    val expressionReference = expressions[indexReference]
                    expressions.forEachIndexed { index, it ->
                        if (index != indexReference) {
                            LangResolverCommon.inferWidthIfBit(expressionReference, it)
                            LangResolverCommon.matchTypes(expressionReference, it)
                        }
                    }
                }
                expressions[0].getTypeGenerifiedNotNull()
            }
            else -> typeSymbol.toTypeGenerified()
        }
    }

    private fun findClosestCommonParent(parentTypeSymbols: List<List<Symbol>>): Symbol {
        val parentTypeSymbolsReversed = parentTypeSymbols.map { it.reversed() }
        val size = parentTypeSymbols.minOf { it.size }
        var typeSymbol = TYPE_ANY
        for (i in 0 until size) {
            if (parentTypeSymbolsReversed.map { it[i] }.distinct().size == 1) {
                typeSymbol = parentTypeSymbolsReversed[0][i]
            } else return typeSymbol
        }
        return typeSymbol
    }
}