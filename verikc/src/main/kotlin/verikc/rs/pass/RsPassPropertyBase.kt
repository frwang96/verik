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
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.PROPERTY_THIS
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsPassPropertyBase: RsPassBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        type.enumProperties.forEach {
            if (it.typeGenerified == null) {
                it.typeGenerified = type.symbol.toTypeGenerified()
                symbolTable.setProperty(it)
            }
        }
        type.functions.forEach {
            passBlock(it.block, type.symbol.toTypeGenerified())
        }
    }

    private fun passBlock(block: RsBlock, typeGenerified: TypeGenerified) {
        for (statement in block.statements) {
            when (statement) {
                is RsStatementDeclaration -> statement.property.expression?.let { passExpression(it, typeGenerified) }
                is RsStatementExpression -> passExpression(statement.expression, typeGenerified)
            }
        }
    }

    private fun passExpression(expression: RsExpression, typeGenerified: TypeGenerified) {
        when (expression) {
            is RsExpressionFunction -> {
                expression.receiver?.let { passExpression(it, typeGenerified) }
                expression.args.map { passExpression(it, typeGenerified) }
            }
            is RsExpressionOperator -> {
                expression.receiver?.let { passExpression(it, typeGenerified) }
                expression.args.map { passExpression(it, typeGenerified) }
                expression.blocks.map { passBlock(it, typeGenerified) }
            }
            is RsExpressionProperty -> {
                expression.receiver?.let { passExpression(it, typeGenerified) }
                if (expression.identifier == "this") {
                    expression.propertySymbol = PROPERTY_THIS
                    expression.typeGenerified = typeGenerified
                    expression.expressionClass = VALUE
                }
            }
            is RsExpressionLiteral -> {}
        }
    }
}