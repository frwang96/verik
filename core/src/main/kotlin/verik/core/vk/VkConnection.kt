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

data class VkConnection(
        override val line: Int,
        val receiver: Symbol,
        val property: Symbol
): Line {

    companion object {

        operator fun invoke(statement: KtStatement): VkConnection {
            return if (statement is KtStatementExpression
                    && statement.expression is KtExpressionFunction
                    && statement.expression.function == FUNCTION_CON) {
                val receiver = statement.expression.receiver
                        ?: throw LineException("con expression receiver expected", statement)
                val property = statement.expression.args[0]
                VkConnection(
                        statement.line,
                        getProperty(receiver),
                        getProperty(property)
                )
            } else throw LineException("con expression expected", statement)
        }

        private fun getProperty(expression: KtExpression): Symbol {
            return if (expression is KtExpressionProperty) {
                expression.property
                        ?: throw LineException("property expression has not been resolved", expression)
            } else {
                throw LineException("property expression expected", expression)
            }
        }
    }
}