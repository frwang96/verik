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

import verik.core.lang.Lang
import verik.core.lang.LangFunctionInstantiatorRequest
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
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
                throw LineException("instantiation of property expression not supported", expression)
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
        val target = expression.target?.let { instantiate(it) }
        val args = expression.args.map { instantiate(it) }
        return Lang.functionTable.instantiate(LangFunctionInstantiatorRequest(
                expression,
                target,
                args
        ))
    }

    private fun instantiateLiteral(expression: VkExpressionLiteral): ItExpressionLiteral {
        val typeInstance = when (expression.type) {
            TYPE_BOOL -> ItTypeInstance(TYPE_BOOL, ItTypeClass.INSTANCE, listOf())
            TYPE_INT -> ItTypeInstance(TYPE_INT, ItTypeClass.INT, listOf())
            else -> throw LineException("bool or Int type expected", expression)
        }
        return ItExpressionLiteral(
                expression.line,
                typeInstance,
                expression.value
        )
    }
}