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

package verikc.rs.pass

import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.rs.ast.*
import verikc.rs.resolve.RsResolverLiteral
import verikc.rs.table.RsSymbolTable

object RsPassExpression {

    fun pass(expression: RsExpression, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        when (expression) {
            is RsExpressionFunction -> passFunction(expression, scopeSymbol, symbolTable)
            is RsExpressionOperator -> passOperator(expression, scopeSymbol, symbolTable)
            is RsExpressionProperty -> passProperty(expression, scopeSymbol, symbolTable)
            is RsExpressionString -> passString(expression, scopeSymbol, symbolTable)
            is RsExpressionLiteral -> RsResolverLiteral.resolve(expression)
        }
        if (expression.typeGenerified == null || expression.expressionClass == null) {
            throw LineException("could not resolve expression", expression.line)
        }
    }

    private fun passFunction(expression: RsExpressionFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        expression.receiver?.let { pass(it, scopeSymbol, symbolTable) }
        expression.args.forEach { pass(it, scopeSymbol, symbolTable) }

        val resolverResult = symbolTable.resolveFunction(expression, scopeSymbol)
        expression.functionSymbol = resolverResult.symbol
        expression.typeGenerified = resolverResult.typeGenerified
        expression.expressionClass = resolverResult.expressionClass
    }

    private fun passOperator(expression: RsExpressionOperator, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        expression.receiver?.let { pass(it, scopeSymbol, symbolTable) }
        expression.args.forEach { pass(it, scopeSymbol, symbolTable) }

        val hasLambdaProperties = expression.blocks.any { it.lambdaProperties.isNotEmpty() }

        // expression type may depend on block
        if (!hasLambdaProperties) {
            expression.blocks.forEach { RsPassBlock.pass(it, symbolTable) }
        }

        val resolverResult = symbolTable.resolveOperator(expression)
        expression.typeGenerified = resolverResult.typeGenerified
        expression.expressionClass = resolverResult.expressionClass

        // lambda parameter type may depend on operator
        if (hasLambdaProperties) {
            expression.blocks.forEach { RsPassBlock.pass(it, symbolTable) }
        }
    }

    private fun passProperty(expression: RsExpressionProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        expression.receiver?.let { pass(it, scopeSymbol, symbolTable) }
        val resolverResult = symbolTable.resolveProperty(expression, scopeSymbol)
        expression.propertySymbol = resolverResult.symbol
        expression.typeGenerified = resolverResult.typeGenerified
        expression.expressionClass = resolverResult.expressionClass
    }

    private fun passString(expression: RsExpressionString, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        for (segment in expression.segments) {
            if (segment is RsStringSegmentExpression) {
                pass(segment.expression, scopeSymbol, symbolTable)
            }
        }
        expression.typeGenerified = TYPE_STRING.toTypeGenerified()
        expression.expressionClass = VALUE
    }
}
