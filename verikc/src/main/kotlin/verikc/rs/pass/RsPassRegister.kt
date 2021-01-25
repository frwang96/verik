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

import verikc.base.symbol.Symbol
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsPassRegister: RsPassBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(type.symbol, scopeSymbol, type.line)
        symbolTable.addType(type, scopeSymbol)

        type.parameterProperties.forEach { passProperty(it, scopeSymbol, symbolTable) }
        passProperty(type.typeObject, scopeSymbol, symbolTable)
        type.topObject?.let { passProperty(it, scopeSymbol, symbolTable) }
        passFunction(type.typeConstructor, scopeSymbol, symbolTable)
        type.instanceConstructor?.let { passFunction(it, scopeSymbol, symbolTable) }

        type.functions.forEach { passFunction(it, type.symbol, symbolTable) }
        type.properties.forEach { passProperty(it, type.symbol, symbolTable) }
        type.enumProperties.forEach { passProperty(it, type.symbol, symbolTable) }
    }

    override fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(function.symbol, scopeSymbol, function.line)
        function.parameterProperties.forEach { passProperty(it, function.symbol, symbolTable) }
        passBlock(function.block, function.symbol, symbolTable)
    }

    override fun passProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (property.expression != null) {
            passExpression(property.expression, scopeSymbol, symbolTable)
        }
        symbolTable.addProperty(property, scopeSymbol)
    }

    private fun passBlock(block: RsBlock, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(block.symbol, scopeSymbol, block.line)
        block.lambdaProperties.forEach { passProperty(it, block.symbol, symbolTable) }
        block.statements.forEach {
            when (it) {
                is RsStatementDeclaration -> {
                    passProperty(it.property, block.symbol, symbolTable)
                }
                is RsStatementExpression -> {
                    passExpression(it.expression, block.symbol, symbolTable)
                }
            }
        }
    }

    private fun passExpression(expression: RsExpression, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        when (expression) {
            is RsExpressionFunction -> {
                expression.args.forEach { passExpression(it, scopeSymbol, symbolTable) }
                expression.receiver?.let { passExpression(it, scopeSymbol, symbolTable) }
            }
            is RsExpressionOperator -> {
                expression.args.forEach { passExpression(it, scopeSymbol, symbolTable) }
                expression.receiver?.let { passExpression(it, scopeSymbol, symbolTable) }
                expression.blocks.forEach { passBlock(it, scopeSymbol, symbolTable) }
            }
            is RsExpressionProperty -> {}
            is RsExpressionLiteral -> {}
        }
    }
}
