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

package verik.core.vk

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.FUNCTION_CON
import verik.core.lang.modules.LangModuleAssignment

enum class VkConnectionType{
    INPUT,
    OUTPUT,
    INOUT
}

data class VkConnection(
        override val line: Int,
        val port: Symbol,
        val connection: Symbol,
        val type: VkConnectionType
): Line {

    companion object {

        operator fun invoke(statement: KtStatement, receiver: Symbol): VkConnection {
            return if (statement is KtStatementExpression
                    && statement.expression is KtExpressionFunction) {
                val isUnidirectional = isUnidirectional(statement.expression.function!!, statement)

                val leftExpression = statement.expression.receiver!!
                val rightExpression = statement.expression.args[0]

                val leftPort = getPort(leftExpression, receiver)
                val leftConnection = getConnection(leftExpression)
                val rightPort = getPort(rightExpression, receiver)
                val rightConnection = getConnection(rightExpression)

                val port = leftPort
                        ?: rightPort
                        ?: throw LineException("could not identify port expression", statement)
                val connection = leftConnection
                        ?: rightConnection
                        ?: throw LineException("could not identify connection expression", statement)

                val type = when {
                    !isUnidirectional -> VkConnectionType.INOUT
                    leftPort != null -> VkConnectionType.INPUT
                    else -> VkConnectionType.OUTPUT
                }
                VkConnection(
                        statement.line,
                        port,
                        connection,
                        type
                )
            } else throw LineException("connection statement expected", statement)
        }

        private fun isUnidirectional(function: Symbol, line: Line): Boolean {
            return when {
                LangModuleAssignment.isBlockAssign(function) -> true
                LangModuleAssignment.isNonblockAssign(function) -> {
                    throw LineException("non-blocking assignment not permitted here", line)
                }
                function == FUNCTION_CON -> false
                else -> throw LineException("invalid connection statement", line)
            }
        }

        private fun getPort(expression: KtExpression, receiver: Symbol): Symbol? {
            return if (expression is KtExpressionProperty && expression.receiver != null) {
                if (expression.receiver is KtExpressionProperty && expression.receiver.property == receiver) {
                    expression.property!!
                } else null
            } else null
        }

        private fun getConnection(expression: KtExpression): Symbol? {
            return if (expression is KtExpressionProperty && expression.receiver == null) {
                expression.property!!
            } else null
        }
    }
}