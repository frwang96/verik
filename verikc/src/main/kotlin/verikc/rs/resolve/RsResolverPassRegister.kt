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

package verikc.rs.resolve

import verikc.base.symbol.Symbol
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsResolverPassRegister: RsResolverPassBase() {

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(type.symbol, scopeSymbol, type.line)
        symbolTable.addType(type, scopeSymbol)

        type.parameterProperties.forEach { resolveProperty(it, scopeSymbol, symbolTable) }
        resolveProperty(type.typeObject, scopeSymbol, symbolTable)
        resolveFunction(type.typeConstructorFunction, scopeSymbol, symbolTable)
        if (type.enumConstructorFunction != null) {
            resolveFunction(type.enumConstructorFunction, scopeSymbol, symbolTable)
        }

        type.functions.forEach { resolveFunction(it, type.symbol, symbolTable) }
        type.properties.forEach { resolveProperty(it, type.symbol, symbolTable) }
        type.enumProperties.forEach { resolveProperty(it, type.symbol, symbolTable) }
    }

    override fun resolveFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(function.symbol, scopeSymbol, function.line)
        function.parameterProperties.forEach { resolveProperty(it, function.symbol, symbolTable) }
        resolveBlock(function.block, function.symbol, symbolTable)
    }

    override fun resolveProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (property.expression != null) {
            resolveExpression(property.expression, scopeSymbol, symbolTable)
        }
        symbolTable.addProperty(property, scopeSymbol)
    }

    private fun resolveBlock(block: RsBlock, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(block.symbol, scopeSymbol, block.line)
        block.lambdaProperties.forEach { resolveProperty(it, block.symbol, symbolTable) }
        block.statements.forEach {
            when (it) {
                is RsStatementDeclaration -> {
                    resolveProperty(it.property, block.symbol, symbolTable)
                }
                is RsStatementExpression -> {
                    resolveExpression(it.expression, block.symbol, symbolTable)
                }
            }
        }
    }

    private fun resolveExpression(expression: RsExpression, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        when (expression) {
            is RsExpressionFunction -> {
                expression.args.forEach { resolveExpression(it, scopeSymbol, symbolTable) }
                expression.receiver?.let { resolveExpression(it, scopeSymbol, symbolTable) }
            }
            is RsExpressionOperator -> {
                expression.args.forEach { resolveExpression(it, scopeSymbol, symbolTable) }
                expression.receiver?.let { resolveExpression(it, scopeSymbol, symbolTable) }
                expression.blocks.forEach { resolveBlock(it, scopeSymbol, symbolTable) }
            }
            is RsExpressionProperty -> {}
            is RsExpressionString -> {
                expression.segments.forEach { segment ->
                    if (segment is RsStringSegmentExpression) {
                        resolveExpression(segment.expression, scopeSymbol, symbolTable)
                    }
                }
            }
            is RsExpressionLiteral -> {}
        }
    }
}
