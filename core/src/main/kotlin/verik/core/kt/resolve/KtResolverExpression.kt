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

package verik.core.kt.resolve

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.Lang
import verik.core.lang.LangSymbol.TYPE_STRING

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

    private fun resolveFunction(expression: KtExpressionFunction, scope: Symbol, symbolTable: KtSymbolTable) {
        expression.target?.let { resolve(it, scope, symbolTable) }
        expression.args.forEach { resolve(it, scope, symbolTable) }

        if (expression.target == null) {
            val functionEntry = symbolTable.resolveFunction(expression, scope)
            expression.function = functionEntry.symbol
            expression.type = functionEntry.returnType
                    ?: throw LineException("function ${expression.identifier} has not been resolved", expression)
        } else {
            val resolvedFunction = Lang.functionTable.resolve(expression)
            expression.function = resolvedFunction.symbol
            expression.type = resolvedFunction.returnType
        }
    }

    private fun resolveOperator(expression: KtExpressionOperator, scope: Symbol, symbolTable: KtSymbolTable) {
        expression.target?.let { resolve(it, scope, symbolTable) }
        expression.args.forEach { resolve(it, scope, symbolTable) }
        expression.blocks.forEach { resolveBlock(it, scope, symbolTable) }
        expression.type = Lang.operatorTable.resolve(expression)
    }

    private fun resolveProperty(expression: KtExpressionProperty, scope: Symbol, symbolTable: KtSymbolTable) {
        if (expression.target != null) {
            throw LineException("resolving of properties with targets not supported", expression)
        }
        val propertyEntry = symbolTable.resolveProperty(expression.identifier, scope, expression.line)
        expression.property = propertyEntry.symbol
        expression.type = propertyEntry.type
                ?: throw LineException("property ${expression.identifier} has not been resolved", expression)
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

    private fun resolveBlock(block: KtBlock, scope: Symbol, symbolTable: KtSymbolTable) {
        block.statements.forEach {
            if (it is KtStatementExpression) {
                resolve(
                        it.expression,
                        scope,
                        symbolTable
                )
            }
        }
    }
}