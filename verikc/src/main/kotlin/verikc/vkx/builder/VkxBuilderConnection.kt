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

package verikc.vkx.builder

import verikc.base.ast.ConnectionType
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.gex.ast.*
import verikc.lang.LangSymbol.FUNCTION_CON_DATA_DATA
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.vkx.ast.VkxConnection

object VkxBuilderConnection {

    fun build(statement: GexStatement, receiverSymbol: Symbol): VkxConnection {
        return if (statement is GexStatementExpression && statement.expression is GexExpressionFunction) {
            val isUnidirectional = isUnidirectional(statement.expression.functionSymbol, statement.line)

            val leftExpression = statement.expression.receiver!!
            val rightExpression = statement.expression.args[0]

            val leftPortSymbol = getPortSymbol(leftExpression, receiverSymbol)
            val leftConnectionSymbol = getConnectionSymbol(leftExpression)
            val rightPortSymbol = getPortSymbol(rightExpression, receiverSymbol)
            val rightConnectionSymbol = getConnectionSymbol(rightExpression)

            val portSymbol = leftPortSymbol
                ?: rightPortSymbol
                ?: throw LineException("could not identify port expression", statement.line)
            val connectionSymbol = leftConnectionSymbol
                ?: rightConnectionSymbol
                ?: throw LineException("could not identify connection expression", statement.line)

            val type = when {
                !isUnidirectional -> ConnectionType.INOUT
                leftPortSymbol != null -> ConnectionType.INPUT
                else -> ConnectionType.OUTPUT
            }
            VkxConnection(
                statement.line,
                portSymbol,
                connectionSymbol,
                type
            )
        } else throw LineException("connection statement expected", statement.line)
    }

    private fun isUnidirectional(functionSymbol: Symbol, line: Line): Boolean {
        return when (functionSymbol) {
            FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE -> true
            FUNCTION_CON_DATA_DATA -> false
            else -> throw LineException("invalid connection statement", line)
        }
    }

    private fun getPortSymbol(expression: GexExpression, receiverSymbol: Symbol): Symbol? {
        return if (expression is GexExpressionProperty && expression.receiver != null) {
            if (expression.receiver is GexExpressionProperty && expression.receiver.propertySymbol == receiverSymbol) {
                expression.propertySymbol
            } else null
        } else null
    }

    private fun getConnectionSymbol(expression: GexExpression): Symbol? {
        return if (expression is GexExpressionProperty && expression.receiver == null) {
            expression.propertySymbol
        } else null
    }
}
