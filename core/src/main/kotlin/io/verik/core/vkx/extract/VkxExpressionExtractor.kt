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

package io.verik.core.vkx.extract

import io.verik.core.lang.Lang
import io.verik.core.main.LineException
import io.verik.core.svx.SvxExpression
import io.verik.core.vkx.*

object VkxExpressionExtractor {

    fun extract(expression: VkxExpression): SvxExpression {
        return when (expression) {
            is VkxExpressionFunction -> extractFunction(expression)
            is VkxExpressionOperator -> throw LineException("operator extraction is not supported", expression)
            is VkxExpressionProperty -> throw LineException("property extraction is not supported", expression)
            is VkxExpressionString -> throw LineException("string extraction is not supported", expression)
            is VkxExpressionLiteral -> throw LineException("literal extraction is not supported", expression)
        }
    }

    private fun extractFunction(expression: VkxExpressionFunction): SvxExpression {
        val args = expression.args.map { it.extract() }
        return Lang.functionTable.extract(expression, args)
                ?: throw LineException("could not extract function", expression)
    }
}