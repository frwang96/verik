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
import verik.core.lang.Lang
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
        if (expression.typeReified == null) {
            throw LineException("could not reify expression", expression)
        }
    }

    private fun reifyFunction(expression: ItExpressionFunction, symbolTable: ItSymbolTable) {
        expression.target?.let { reify(it, symbolTable) }
        expression.args.map { reify(it, symbolTable) }
        Lang.functionTable.reify(expression)
    }

    private fun reifyOperator(expression: ItExpressionOperator, symbolTable: ItSymbolTable) {
        expression.target?.let { reify(it, symbolTable) }
        expression.args.map { reify(it, symbolTable) }
        expression.blocks.map { reifyBlock(it, symbolTable) }
        expression.typeReified = Lang.operatorTable.reify(expression)
    }

    private fun reifyProperty(expression: ItExpressionProperty, symbolTable: ItSymbolTable) {
        if (expression.target != null) {
            throw LineException("reification of property with target expression not supported", expression)
        }
        val resolvedProperty = symbolTable.getProperty(expression)
        val typeReified = resolvedProperty.typeReified
                ?: throw LineException("property has not been reified", expression)
        expression.typeReified = typeReified
    }

    private fun reifyString(expression: ItExpressionString, symbolTable: ItSymbolTable) {
        expression.typeReified = ItTypeReified(TYPE_STRING, ItTypeClass.INSTANCE, listOf())
        for (segment in expression.segments) {
            if (segment is ItStringSegmentExpression) {
                reify(segment.expression, symbolTable)
            }
        }
    }

    private fun reifyLiteral(expression: ItExpressionLiteral) {
        expression.typeReified = when (expression.type) {
            TYPE_BOOL -> ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf())
            TYPE_INT -> ItTypeReified(TYPE_INT, ItTypeClass.INSTANCE, listOf())
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