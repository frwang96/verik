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

package verik.core.it

import verik.core.main.LineException
import verik.core.vk.*

object ItExpressionInstantiator {

    fun instantiate(expression: VkExpression): ItExpression {
        return when (expression) {
            is VkExpressionFunction -> {
                instantiateFunction(expression)
            }
            is VkExpressionOperator -> {
                throw LineException("instantiation of operator expression not supported", expression)
            }
            is VkExpressionProperty -> {
                instantiateProperty(expression)
            }
            is VkExpressionString -> {
                throw LineException("instantiation of string expression not supported", expression)
            }
            is VkExpressionLiteral -> {
                instantiateLiteral(expression)
            }
        }
    }

    private fun instantiateFunction(expression: VkExpressionFunction): ItExpressionFunction {
        return ItExpressionFunction(
                expression.line,
                expression.type,
                null,
                expression.function,
                expression.target?.let { instantiate(it) },
                expression.args.map { instantiate(it) }
        )
    }

    private fun instantiateProperty(expression: VkExpressionProperty): ItExpressionProperty {
        val target = expression.target?.let { instantiate(it) }
        return ItExpressionProperty(
                expression.line,
                expression.type,
                null,
                expression.property,
                target
        )
    }

    private fun instantiateLiteral(expression: VkExpressionLiteral): ItExpressionLiteral {
        return ItExpressionLiteral(
                expression.line,
                expression.type,
                null,
                expression.value
        )
    }
}