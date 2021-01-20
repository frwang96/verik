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

package verikc.vk.build

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.util.LangSymbolUtil
import verikc.rs.ast.*
import verikc.vk.ast.VkConnection
import verikc.vk.ast.VkConnectionType
import verikc.vk.ast.VkExpression

object VkBuilderConnection {

    fun build(statement: RsStatement, receiverSymbol: Symbol): VkConnection {
        return if (statement is RsStatementExpression && statement.expression is RsExpressionFunction) {
            val isUnidirectional = isUnidirectional(statement.expression.getFunctionSymbolNotNull(), statement.line)

            val leftExpression = statement.expression.receiver!!
            val rightExpression = statement.expression.args[0]

            val leftPortSymbol = getPortSymbol(leftExpression, receiverSymbol)
            val rightPortSymbol = getPortSymbol(rightExpression, receiverSymbol)

            when {
                leftPortSymbol != null && rightPortSymbol == null -> {
                    VkConnection(
                        statement.line,
                        leftPortSymbol,
                        if (isUnidirectional) VkConnectionType.INPUT else VkConnectionType.INOUT,
                        identifiersMatch(leftExpression, rightExpression),
                        VkExpression(rightExpression),
                        null
                    )
                }
                leftPortSymbol == null && rightPortSymbol != null -> {
                    VkConnection(
                        statement.line,
                        rightPortSymbol,
                        if (isUnidirectional) VkConnectionType.OUTPUT else VkConnectionType.INOUT,
                        identifiersMatch(rightExpression, leftExpression),
                        VkExpression(leftExpression),
                        null
                    )
                }
                leftPortSymbol == null && rightPortSymbol == null ->
                    throw LineException("could not identify port expression", statement.line)
                else ->
                    throw LineException("invalid connection statement", statement.line)
            }
        } else throw LineException("connection statement expected", statement.line)
    }

    private fun isUnidirectional(functionSymbol: Symbol, line: Line): Boolean {
        return when {
            LangSymbolUtil.isConFunction(functionSymbol) -> false
            functionSymbol == FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE -> true
            else -> throw LineException("invalid connection statement", line)
        }
    }

    private fun getPortSymbol(expression: RsExpression, receiverSymbol: Symbol): Symbol? {
        return if (expression is RsExpressionProperty && expression.receiver != null) {
            if (expression.receiver is RsExpressionProperty && expression.receiver.propertySymbol == receiverSymbol) {
                expression.propertySymbol
            } else null
        } else null
    }

    private fun identifiersMatch(portExpression: RsExpression, expression: RsExpression): Boolean {
        val portIdentifier = if (portExpression is RsExpressionProperty) {
            portExpression.identifier
        } else null
        val identifier = if (expression is RsExpressionProperty && expression.receiver == null) {
            expression.identifier
        } else null
        return (portIdentifier != null && identifier != null && portIdentifier == identifier)
    }
}
