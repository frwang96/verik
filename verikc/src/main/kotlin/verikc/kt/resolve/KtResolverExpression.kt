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
import verikc.base.ast.Symbol
import verikc.kt.ast.*
import verikc.kt.symbol.KtSymbolTable
import verikc.lang.LangSymbol.TYPE_STRING

object KtResolverExpression {

    fun resolve(expression: KtExpression, scope: Symbol, symbolTable: KtSymbolTable) {
        when (expression) {
            is KtExpressionFunction -> resolveFunction(expression, scope, symbolTable)
            is KtExpressionOperator -> resolveOperator(expression, scope, symbolTable)
            is KtExpressionProperty -> resolveProperty(expression, scope, symbolTable)
            is KtExpressionString -> resolveString(expression, scope, symbolTable)
            is KtExpressionLiteral -> resolveLiteral(expression)
        }
        if (expression.type == null) {
            throw LineException("could not resolve expression", expression)
        }
    }

    fun resolveBlock(block: KtBlock, scope: Symbol, symbolTable: KtSymbolTable) {
        symbolTable.addScope(block.symbol, scope, block.line)
        block.lambdaProperties.forEach {
            symbolTable.addProperty(it, block.symbol)
        }
        block.statements.forEach {
            if (it is KtStatementExpression) {
                resolve(it.expression, block.symbol, symbolTable)
            }
        }
    }

    private fun resolveFunction(expression: KtExpressionFunction, scope: Symbol, symbolTable: KtSymbolTable) {
        expression.receiver?.let { resolve(it, scope, symbolTable) }
        expression.args.forEach { resolve(it, scope, symbolTable) }

        val functionEntry = symbolTable.resolveFunction(expression, scope)
        expression.function = functionEntry.symbol
        expression.type = functionEntry.type
    }

    private fun resolveOperator(expression: KtExpressionOperator, scope: Symbol, symbolTable: KtSymbolTable) {
        expression.receiver?.let { resolve(it, scope, symbolTable) }
        expression.args.forEach { resolve(it, scope, symbolTable) }

        val hasLambdaProperties = expression.blocks.any { it.lambdaProperties.isNotEmpty() }

        // expression type may depend on block
        if (!hasLambdaProperties) {
            expression.blocks.forEach { resolveBlock(it, scope, symbolTable) }
        }

        expression.type = symbolTable.resolveOperator(expression)

        // lambda parameter type may depend on operator
        if (hasLambdaProperties) {
            expression.blocks.forEach { resolveBlock(it, scope, symbolTable) }
        }
    }

    private fun resolveProperty(expression: KtExpressionProperty, scope: Symbol, symbolTable: KtSymbolTable) {
        expression.receiver?.let { resolve(it, scope, symbolTable) }
        val propertyEntry = symbolTable.resolveProperty(expression, scope)
        expression.property = propertyEntry.symbol
        expression.type = propertyEntry.type
    }

    private fun resolveString(expression: KtExpressionString, scope: Symbol, symbolTable: KtSymbolTable) {
        expression.type = TYPE_STRING
        for (segment in expression.segments) {
            if (segment is KtStringSegmentExpression) {
                resolve(segment.expression, scope, symbolTable)
            }
        }
    }

    private fun resolveLiteral(expression: KtExpressionLiteral) {
        expression.type
                ?: throw LineException("literal expression has not been resolved", expression)
    }
}
