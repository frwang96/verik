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

package verikc.ge.generify

import verikc.base.ast.LineException
import verikc.ge.ast.*
import verikc.ge.symbol.GeSymbolTable
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_STRING

object GeGenerifierExpression {

    fun generify(expression: GeExpression, symbolTable: GeSymbolTable) {
        when (expression) {
            is GeExpressionFunction -> generifyFunction(expression, symbolTable)
            is GeExpressionOperator -> generifyOperator(expression, symbolTable)
            is GeExpressionProperty -> generifyProperty(expression, symbolTable)
            is GeExpressionString -> generifyString(expression, symbolTable)
            is GeExpressionLiteral -> generifyLiteral(expression)
        }
        if (expression.typeGenerified == null) {
            throw LineException("could not generify expression", expression.line)
        }
    }

    private fun generifyFunction(expression: GeExpressionFunction, symbolTable: GeSymbolTable) {
        expression.receiver?.let { generify(it, symbolTable) }
        expression.args.forEach { generify(it, symbolTable) }
        expression.typeGenerified = symbolTable.generifyFunction(expression)
    }

    private fun generifyOperator(expression: GeExpressionOperator, symbolTable: GeSymbolTable) {
        expression.receiver?.let { generify(it, symbolTable) }
        expression.args.forEach { generify(it, symbolTable) }
        expression.blocks.forEach { GeGenerifierBlock.generify(it, symbolTable) }
        expression.typeGenerified = symbolTable.generifyOperator(expression)
    }

    private fun generifyProperty(expression: GeExpressionProperty, symbolTable: GeSymbolTable) {
        expression.receiver?.let { generify(it, symbolTable) }
        expression.typeGenerified = symbolTable.generifyProperty(expression)
    }

    private fun generifyString(expression: GeExpressionString, symbolTable: GeSymbolTable) {
        expression.typeGenerified = TYPE_STRING.toTypeGenerifiedInstance()
        for (segment in expression.segments) {
            if (segment is GeStringSegmentExpression) {
                generify(segment.expression, symbolTable)
            }
        }
    }

    private fun generifyLiteral(expression: GeExpressionLiteral) {
        expression.typeGenerified = when (expression.typeSymbol) {
            TYPE_BOOL -> TYPE_BOOL.toTypeGenerifiedInstance()
            TYPE_INT -> TYPE_INT.toTypeGenerifiedInstance()
            else -> throw LineException("bool or int type expected", expression.line)
        }
    }
}
