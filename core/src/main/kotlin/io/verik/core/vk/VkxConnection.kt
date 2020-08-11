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

package io.verik.core.vk

import io.verik.core.Line
import io.verik.core.LineException
import io.verik.core.kt.KtExpressionOperator
import io.verik.core.kt.KtOperatorIdentifier
import io.verik.core.kt.KtStatement
import io.verik.core.symbol.Symbol

data class VkxConnection(
        override val line: Int,
        val target: Symbol?,
        val expression: VkxExpression
): Line {

    companion object {

        operator fun invoke(statement: KtStatement): VkxConnection {
            return if (statement.expression is KtExpressionOperator && statement.expression.identifier == KtOperatorIdentifier.INFIX_CON) {
                val expression = VkxExpression(statement.expression.args[0])
                VkxConnection(
                        statement.line,
                        null,
                        expression
                )
            } else throw LineException("con expression expected", statement)
        }
    }
}