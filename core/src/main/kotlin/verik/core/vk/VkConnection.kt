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
import verik.core.kt.KtExpressionFunction
import verik.core.kt.KtExpressionProperty
import verik.core.kt.KtStatement
import verik.core.kt.KtStatementExpression
import verik.core.lang.LangSymbol.FUNCTION_CON

data class VkConnection(
        override val line: Int,
        val receiver: Symbol,
        val expression: VkExpression
): Line {

    companion object {

        operator fun invoke(statement: KtStatement): VkConnection {
            return if (statement is KtStatementExpression
                    && statement.expression is KtExpressionFunction
                    && statement.expression.function == FUNCTION_CON) {
                val receiverExpression = statement.expression.receiver!!
                val receiver = if (receiverExpression is KtExpressionProperty) {
                    receiverExpression.property!!
                } else throw LineException("property expression expected as receiver of con expression", statement)
                VkConnection(
                        statement.line,
                        receiver,
                        VkExpression(statement.expression.args[0])
                )
            } else throw LineException("con expression expected", statement)
        }
    }
}