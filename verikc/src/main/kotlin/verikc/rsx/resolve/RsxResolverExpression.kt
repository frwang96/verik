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

package verikc.rsx.resolve

import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.rsx.ast.*
import verikc.rsx.table.RsxSymbolTable

object RsxResolverExpression {

    fun resolve(expression: RsxExpression, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        when (expression) {
            is RsxExpressionFunction -> resolveFunction(expression, scopeSymbol, symbolTable)
            is RsxExpressionOperator -> resolveOperator(expression, scopeSymbol, symbolTable)
            is RsxExpressionProperty -> resolveProperty(expression, scopeSymbol, symbolTable)
            is RsxExpressionString -> resolveString(expression, scopeSymbol, symbolTable)
            is RsxExpressionLiteral -> RsxResolverLiteral.resolve(expression)
        }
        if (expression.typeGenerified == null || expression.expressionClass == null) {
            throw LineException("could not resolve expression", expression.line)
        }
    }

    private fun resolveFunction(expression: RsxExpressionFunction, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        expression.receiver?.let { resolve(it, scopeSymbol, symbolTable) }
        expression.args.forEach { resolve(it, scopeSymbol, symbolTable) }

        val resolverResult = symbolTable.resolveFunction(expression, scopeSymbol)
        expression.functionSymbol = resolverResult.symbol
        expression.typeGenerified = resolverResult.typeGenerified
        expression.expressionClass = resolverResult.expressionClass
    }

    private fun resolveOperator(expression: RsxExpressionOperator, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        expression.receiver?.let { resolve(it, scopeSymbol, symbolTable) }
        expression.args.forEach { resolve(it, scopeSymbol, symbolTable) }

        val hasLambdaProperties = expression.blocks.any { it.lambdaProperties.isNotEmpty() }

        // expression type may depend on block
        if (!hasLambdaProperties) {
            expression.blocks.forEach { RsxResolverBlock.resolve(it, scopeSymbol, symbolTable) }
        }

        val resolverResult = symbolTable.resolveOperator(expression)
        expression.typeGenerified = resolverResult.typeGenerified
        expression.expressionClass = resolverResult.expressionClass

        // lambda parameter type may depend on operator
        if (hasLambdaProperties) {
            expression.blocks.forEach { RsxResolverBlock.resolve(it, scopeSymbol, symbolTable) }
        }
    }

    private fun resolveProperty(expression: RsxExpressionProperty, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        expression.receiver?.let { resolve(it, scopeSymbol, symbolTable) }
        val resolverResult = symbolTable.resolveProperty(expression, scopeSymbol)
        expression.propertySymbol = resolverResult.symbol
        expression.typeGenerified = resolverResult.typeGenerified
        expression.expressionClass = resolverResult.expressionClass
    }

    private fun resolveString(expression: RsxExpressionString, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        for (segment in expression.segments) {
            if (segment is RsxStringSegmentExpression) {
                resolve(segment.expression, scopeSymbol, symbolTable)
            }
        }
        expression.typeGenerified = TYPE_STRING.toTypeGenerified()
        expression.expressionClass = VALUE
    }
}
