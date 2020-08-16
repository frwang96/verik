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

import verik.core.kt.KtOperatorIdentifier
import verik.core.ktx.KtxExpressionOperator
import verik.core.ktx.KtxStatement
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

data class VkConnection(
        override val line: Int,
        val target: Symbol?,
        val expression: VkExpression
): Line {

    companion object {

        operator fun invoke(statement: KtxStatement): VkConnection {
            return if (statement.expression is KtxExpressionOperator
                    && statement.expression.identifier == KtOperatorIdentifier.INFIX_CON) {
                val expression = VkExpression(statement.expression.args[0])
                VkConnection(
                        statement.line,
                        null,
                        expression
                )
            } else throw LineException("con expression expected", statement)
        }
    }
}