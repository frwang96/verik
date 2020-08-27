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

package verik.core.it.reify

import verik.core.base.LineException
import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_STRING

object ItReifierExpression {

    fun reify(expression: ItExpression, symbolTable: ItSymbolTable) {
        when (expression) {
            is ItExpressionFunction -> reifyFunction(expression, symbolTable)
            is ItExpressionOperator -> reifyOperator(expression, symbolTable)
            is ItExpressionProperty -> reifyProperty(expression, symbolTable)
            is ItExpressionString -> reifyString(expression, symbolTable)
            is ItExpressionLiteral -> reifyLiteral(expression)
        }
        if (expression.reifiedType == null) {
            throw LineException("could not reify expression", expression)
        }
    }

    private fun reifyFunction(expression: ItExpressionFunction, symbolTable: ItSymbolTable) {
        expression.receiver?.let { reify(it, symbolTable) }
        expression.args.map { reify(it, symbolTable) }
        expression.reifiedType = symbolTable.reifyFunction(expression)
    }

    private fun reifyOperator(expression: ItExpressionOperator, symbolTable: ItSymbolTable) {
        expression.receiver?.let { reify(it, symbolTable) }
        expression.args.map { reify(it, symbolTable) }
        expression.blocks.map { reifyBlock(it, symbolTable) }
        expression.reifiedType = symbolTable.reifyOperator(expression)
    }

    private fun reifyProperty(expression: ItExpressionProperty, symbolTable: ItSymbolTable) {
        if (expression.receiver != null) {
            throw LineException("reification of property with receiver expression not supported", expression)
        }
        expression.reifiedType = symbolTable.reifyProperty(expression)
    }

    private fun reifyString(expression: ItExpressionString, symbolTable: ItSymbolTable) {
        expression.reifiedType = ItReifiedType(TYPE_STRING, ItTypeClass.INSTANCE, listOf())
        for (segment in expression.segments) {
            if (segment is ItStringSegmentExpression) {
                reify(segment.expression, symbolTable)
            }
        }
    }

    private fun reifyLiteral(expression: ItExpressionLiteral) {
        expression.reifiedType = when (expression.type) {
            TYPE_BOOL -> ItReifiedType(TYPE_BOOL, ItTypeClass.INSTANCE, listOf())
            TYPE_INT -> ItReifiedType(TYPE_INT, ItTypeClass.INSTANCE, listOf())
            else -> throw LineException("bool or int type expected", expression)
        }
    }

    private fun reifyBlock(block: ItBlock, symbolTable: ItSymbolTable) {
        block.statements.map {
            if (it is ItStatementExpression) {
                reify(it.expression, symbolTable)
            }
        }
    }
}