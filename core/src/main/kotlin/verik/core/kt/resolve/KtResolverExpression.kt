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

    fun resolve(expression: KtExpression, parent: Symbol, symbolTable: KtSymbolTable) {
        when (expression) {
            is KtExpressionFunction -> resolveFunction(expression, parent, symbolTable)
            is KtExpressionOperator -> resolveOperator(expression, parent, symbolTable)
            is KtExpressionProperty -> resolveProperty(expression, parent, symbolTable)
            is KtExpressionString -> resolveString(expression, parent, symbolTable)
            is KtExpressionLiteral -> resolveLiteral(expression)
        }
        if (expression.type == null) {
            throw LineException("could not resolve expression", expression)
        }
    }

    private fun resolveFunction(expression: KtExpressionFunction, parent: Symbol, symbolTable: KtSymbolTable) {
        expression.target?.let { resolve(it, parent, symbolTable) }
        expression.args.forEach { resolve(it, parent, symbolTable) }
        val resolvedFunction = Lang.functionTable.resolve(expression)
        expression.function = resolvedFunction.symbol
        expression.type = resolvedFunction.returnType
    }

    private fun resolveOperator(expression: KtExpressionOperator, parent: Symbol, symbolTable: KtSymbolTable) {
        expression.target?.let { resolve(it, parent, symbolTable) }
        expression.args.forEach { resolve(it, parent, symbolTable) }
        expression.blocks.forEach { resolveBlock(it, parent, symbolTable) }
        expression.type = Lang.operatorTable.resolve(expression)
    }

    private fun resolveProperty(expression: KtExpressionProperty, parent: Symbol, symbolTable: KtSymbolTable) {
        if (expression.target != null) {
            throw LineException("resolving of properties with targets not supported", expression)
        }
        val resolvedProperty = symbolTable.resolveProperty(parent, expression.identifier, expression.line)
                ?: throw LineException("unable to resolve property ${expression.identifier}", expression.line)
        val type = resolvedProperty.type
                ?: throw LineException("type of property has not been resolved", expression.line)
        expression.property = resolvedProperty.symbol
        expression.type = type
    }

    private fun resolveString(expression: KtExpressionString, parent: Symbol, symbolTable: KtSymbolTable) {
        expression.type = TYPE_STRING
        for (segment in expression.segments) {
            if (segment is KtStringSegmentExpression) {
                resolve(segment.expression, parent, symbolTable)
            }
        }
    }

    private fun resolveLiteral(expression: KtExpressionLiteral) {
        expression.type
                ?: throw LineException("literal expression has not been resolved", expression)
    }

    private fun resolveBlock(block: KtBlock, parent: Symbol, symbolTable: KtSymbolTable) {
        block.statements.forEach {
            if (it is KtStatementExpression) {
                resolve(
                        it.expression,
                        parent,
                        symbolTable
                )
            }
        }
    }
}