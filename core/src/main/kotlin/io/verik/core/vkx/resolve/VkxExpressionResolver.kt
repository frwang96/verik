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

package io.verik.core.vkx.resolve

import io.verik.core.lang.Lang
import io.verik.core.main.LineException
import io.verik.core.vkx.*

object VkxExpressionResolver {

    fun resolve(expression: VkxExpression) {
        when (expression) {
            is VkxExpressionFunction -> {
                expression.args.forEach { resolve(it) }
                expression.vkxType = Lang.functionTable.resolve(expression.function, expression.args)
            }
            is VkxExpressionOperator -> {
                throw LineException("resolving operator expressions is not supported", expression)
            }
            is VkxExpressionProperty -> {
                throw LineException("resolving property expressions is not supported", expression)
            }
            is VkxExpressionString -> {
                throw LineException("resolving string expressions is not supported", expression)
            }
            is VkxExpressionLiteral -> {
                expression.vkxType = VkxType(expression.ktType, listOf())
            }
        }
        if (expression.vkxType == null) {
            throw LineException("could not resolve expression", expression)
        }
    }
}