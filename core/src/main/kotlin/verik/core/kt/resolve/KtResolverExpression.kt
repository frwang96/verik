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

import verik.core.kt.*
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.Lang
import verik.core.lang.LangSymbol
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

object KtResolverExpression: KtResolverBase() {

    override fun resolveType(type: KtDeclarationType, parent: Symbol, symbolTable: KtSymbolTable) {
        type.declarations.forEach { resolveDeclaration(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: KtDeclarationFunction, parent: Symbol, symbolTable: KtSymbolTable) {
        function.block.statements.forEach { resolveExpression(
                it.expression,
                function.symbol,
                symbolTable
        ) }
    }

    fun resolveExpression(expression: KtExpression, parent: Symbol, symbolTable: KtSymbolTable) {
        when (expression) {
            is KtExpressionFunction -> resolveExpressionFunction(expression, parent, symbolTable)
            is KtExpressionOperator -> throw LineException("resolving operator expressions is not supported", expression)
            is KtExpressionProperty -> resolveExpressionProperty(expression, parent, symbolTable)
            is KtExpressionString -> throw LineException("resolving string expressions is not supported", expression)
            is KtExpressionLiteral -> resolveExpressionLiteral(expression)
        }
        if (expression.type == null) {
            throw LineException("could not resolve expression", expression)
        }
    }

    private fun resolveExpressionFunction(expression: KtExpressionFunction, parent: Symbol, symbolTable: KtSymbolTable) {
        expression.target?.let { resolveExpression(it, parent, symbolTable) }
        expression.args.forEach { resolveExpression(it, parent, symbolTable) }
        val argTypes = expression.args.map { it.type!! }
        val resolvedFunction = Lang.functionTable.resolve(expression, argTypes)
        expression.function = resolvedFunction.symbol
        expression.type = resolvedFunction.returnType
    }

    private fun resolveExpressionProperty(expression: KtExpressionProperty, parent: Symbol, symbolTable: KtSymbolTable) {
        if (expression.target != null) {
            throw LineException("resolving of properties with targets not supported", expression)
        }
        val resolvedProperty = symbolTable.resolveProperty(parent, expression.identifier, expression.line)
                ?: throw LineException("unable to resolve property ${expression.identifier}", expression.line)
        val type = resolvedProperty.type
                ?: throw LineException("type of resolved property has not been resolved", expression.line)
        expression.property = resolvedProperty.symbol
        expression.type = type
    }

    private fun resolveExpressionLiteral(expression: KtExpressionLiteral) {
        expression.type = when (expression.value) {
            "true", "false" -> LangSymbol.TYPE_BOOL
            else -> LangSymbol.TYPE_INT
        }
    }
}