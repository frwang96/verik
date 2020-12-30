/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.rf.reify

import verikc.base.ast.LineException
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.rf.ast.*
import verikc.rf.symbol.RfSymbolTable

object RfReifierExpression {

    fun reify(expression: RfExpression, symbolTable: RfSymbolTable) {
        when (expression) {
            is RfExpressionFunction -> reifyFunction(expression, symbolTable)
            is RfExpressionOperator -> reifyOperator(expression, symbolTable)
            is RfExpressionProperty -> reifyProperty(expression, symbolTable)
            is RfExpressionString -> reifyString(expression, symbolTable)
            is RfExpressionLiteral -> reifyLiteral(expression)
        }
        if (expression.typeReified == null) {
            throw LineException("could not reify expression", expression.line)
        }
    }

    private fun reifyFunction(expression: RfExpressionFunction, symbolTable: RfSymbolTable) {
        expression.receiver?.let { reify(it, symbolTable) }
        expression.args.map { reify(it, symbolTable) }
        expression.typeReified = symbolTable.reifyFunction(expression)
    }

    private fun reifyOperator(expression: RfExpressionOperator, symbolTable: RfSymbolTable) {
        expression.receiver?.let { reify(it, symbolTable) }
        expression.args.map { reify(it, symbolTable) }
        expression.blocks.map { reifyBlock(it, symbolTable) }
        expression.typeReified = symbolTable.reifyOperator(expression)
    }

    private fun reifyProperty(expression: RfExpressionProperty, symbolTable: RfSymbolTable) {
        expression.receiver?.let { reify(it, symbolTable) }
        expression.typeReified = symbolTable.reifyProperty(expression)
    }

    private fun reifyString(expression: RfExpressionString, symbolTable: RfSymbolTable) {
        expression.typeReified = TYPE_STRING.toTypeReifiedInstance()
        for (segment in expression.segments) {
            if (segment is RfStringSegmentExpression) {
                reify(segment.expression, symbolTable)
            }
        }
    }

    private fun reifyLiteral(expression: RfExpressionLiteral) {
        expression.typeReified = when (expression.typeSymbol) {
            TYPE_BOOL -> TYPE_BOOL.toTypeReifiedInstance()
            TYPE_INT -> TYPE_INT.toTypeReifiedInstance()
            else -> throw LineException("bool or int type expected", expression.line)
        }
    }

    private fun reifyBlock(block: RfBlock, symbolTable: RfSymbolTable) {
        block.statements.map {
            if (it is RfStatementExpression) {
                reify(it.expression, symbolTable)
            }
        }
    }
}
