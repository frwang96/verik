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
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.PROPERTY_NULL
import verikc.lang.LangSymbol.PROPERTY_THIS
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsPassPropertyBase: RsPassBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        val typeGenerified = type.symbol.toTypeGenerified()
        if (type.instanceConstructor?.block != null) {
            passBlock(type.instanceConstructor.block, typeGenerified)
        }
        type.enumProperties.forEach {
            if (it.typeGenerified == null) {
                it.typeGenerified = typeGenerified
                symbolTable.setProperty(it)
            }
        }
        type.functions.forEach {
            passBlock(it.getBlockNotNull(), typeGenerified)
        }
        type.properties.forEach {
            if (it.expression != null) passExpression(it.expression, typeGenerified)
        }
    }

    override fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        passBlock(function.getBlockNotNull(), null)
    }

    override fun passProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (property.expression != null) passExpression(property.expression, null)
    }

    private fun passBlock(block: RsBlock, typeGenerified: TypeGenerified?) {
        for (statement in block.statements) {
            when (statement) {
                is RsStatementDeclaration -> statement.property.expression?.let { passExpression(it, typeGenerified) }
                is RsStatementExpression -> passExpression(statement.expression, typeGenerified)
            }
        }
    }

    private fun passExpression(expression: RsExpression, typeGenerified: TypeGenerified?) {
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
                when (expression.identifier) {
                    "this" -> {
                        expression.propertySymbol = PROPERTY_THIS
                        expression.typeGenerified = typeGenerified
                            ?: throw LineException("could not resolve this", expression.line)
                        expression.expressionClass = VALUE
                    }
                    "null" -> {
                        expression.propertySymbol = PROPERTY_NULL
                        expression.typeGenerified = TYPE_ANY.toTypeGenerified()
                        expression.expressionClass = VALUE
                    }
                }
            }
            is RsExpressionLiteral -> {}
        }
    }
}