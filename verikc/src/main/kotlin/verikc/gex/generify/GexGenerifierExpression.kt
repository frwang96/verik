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

package verikc.gex.generify

import verikc.base.ast.LineException
import verikc.gex.ast.*
import verikc.gex.table.GexSymbolTable
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_STRING

object GexGenerifierExpression {

    fun generify(expression: GexExpression, symbolTable: GexSymbolTable) {
        when (expression) {
            is GexExpressionFunction -> generifyFunction(expression, symbolTable)
            is GexExpressionOperator -> generifyOperator(expression, symbolTable)
            is GexExpressionProperty -> generifyProperty(expression, symbolTable)
            is GexExpressionString -> generifyString(expression, symbolTable)
            is GexExpressionLiteral -> generifyLiteral(expression)
        }
        if (expression.typeGenerified == null) {
            throw LineException("could not generify expression", expression.line)
        }
    }

    private fun generifyFunction(expression: GexExpressionFunction, symbolTable: GexSymbolTable) {
        expression.receiver?.let { generify(it, symbolTable) }
        expression.args.forEach { generify(it, symbolTable) }
        expression.typeGenerified = symbolTable.generifyFunction(expression)
    }

    private fun generifyOperator(expression: GexExpressionOperator, symbolTable: GexSymbolTable) {
        expression.receiver?.let { generify(it, symbolTable) }
        expression.args.forEach { generify(it, symbolTable) }
        expression.blocks.forEach { GexGenerifierBlock.generify(it, symbolTable) }
        expression.typeGenerified = symbolTable.generifyOperator(expression)
    }

    private fun generifyProperty(expression: GexExpressionProperty, symbolTable: GexSymbolTable) {
        expression.receiver?.let { generify(it, symbolTable) }
        expression.typeGenerified = symbolTable.generifyProperty(expression)
    }

    private fun generifyString(expression: GexExpressionString, symbolTable: GexSymbolTable) {
        expression.typeGenerified = TYPE_STRING.toTypeGenerifiedInstance()
        for (segment in expression.segments) {
            if (segment is GexStringSegmentExpression) {
                generify(segment.expression, symbolTable)
            }
        }
    }

    private fun generifyLiteral(expression: GexExpressionLiteral) {
        expression.typeGenerified = when (expression.typeSymbol) {
            TYPE_BOOL -> TYPE_BOOL.toTypeGenerifiedInstance()
            TYPE_INT -> TYPE_INT.toTypeGenerifiedInstance()
            else -> throw LineException("bool or int type expected", expression.line)
        }
    }
}
