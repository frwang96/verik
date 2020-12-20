/*
 * Copyright 2020 Francis Wang
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
import verikc.lang.LangSymbol.TYPE_STRING

object KtResolverExpression {

    fun resolve(expression: KtExpression, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        when (expression) {
            is KtExpressionFunction -> resolveFunction(expression, scopeSymbol, symbolTable)
            is KtExpressionOperator -> resolveOperator(expression, scopeSymbol, symbolTable)
            is KtExpressionProperty -> resolveProperty(expression, scopeSymbol, symbolTable)
            is KtExpressionString -> resolveString(expression, scopeSymbol, symbolTable)
            is KtExpressionLiteral -> resolveLiteral(expression)
        }
        if (expression.typeSymbol == null) {
            throw LineException("could not resolve expression", expression.line)
        }
    }

    fun resolveBlock(block: KtBlock, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        symbolTable.addScope(block.symbol, scopeSymbol, block.line)
        block.lambdaProperties.forEach {
            symbolTable.addProperty(it, block.symbol)
        }
        block.statements.forEach {
            if (it is KtStatementExpression) {
                resolve(it.expression, block.symbol, symbolTable)
            }
        }
    }

    private fun resolveFunction(expression: KtExpressionFunction, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        expression.receiver?.let { resolve(it, scopeSymbol, symbolTable) }
        expression.args.forEach { resolve(it, scopeSymbol, symbolTable) }

        val functionEntry = symbolTable.resolveFunction(expression, scopeSymbol)
        expression.functionSymbol = functionEntry.symbol
        expression.typeSymbol = functionEntry.type
    }

    private fun resolveOperator(expression: KtExpressionOperator, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        expression.receiver?.let { resolve(it, scopeSymbol, symbolTable) }
        expression.args.forEach { resolve(it, scopeSymbol, symbolTable) }

        val hasLambdaProperties = expression.blocks.any { it.lambdaProperties.isNotEmpty() }

        // expression type may depend on block
        if (!hasLambdaProperties) {
            expression.blocks.forEach { resolveBlock(it, scopeSymbol, symbolTable) }
        }

        expression.typeSymbol = symbolTable.resolveOperator(expression)

        // lambda parameter type may depend on operator
        if (hasLambdaProperties) {
            expression.blocks.forEach { resolveBlock(it, scopeSymbol, symbolTable) }
        }
    }

    private fun resolveProperty(expression: KtExpressionProperty, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        expression.receiver?.let { resolve(it, scopeSymbol, symbolTable) }
        val propertyEntry = symbolTable.resolveProperty(expression, scopeSymbol)
        expression.propertySymbol = propertyEntry.symbol
        expression.typeSymbol = propertyEntry.type
    }

    private fun resolveString(expression: KtExpressionString, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        expression.typeSymbol = TYPE_STRING
        for (segment in expression.segments) {
            if (segment is KtStringSegmentExpression) {
                resolve(segment.expression, scopeSymbol, symbolTable)
            }
        }
    }

    private fun resolveLiteral(expression: KtExpressionLiteral) {
        expression.typeSymbol
            ?: throw LineException("literal expression has not been resolved", expression.line)
    }
}
