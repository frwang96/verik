/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.constant

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression

object BooleanConstantEvaluator {

    fun binaryAndBoolean(original: EExpression, left: EExpression, right: EExpression): EExpression? {
        val leftBoolean = ConstantNormalizer.parseBooleanOrNull(left)
        val rightBoolean = ConstantNormalizer.parseBooleanOrNull(right)
        return when {
            leftBoolean == false || rightBoolean == false -> ConstantBuilder.buildBoolean(original, false)
            leftBoolean == true -> right
            rightBoolean == true -> left
            else -> null
        }
    }

    fun binaryOrBoolean(original: EExpression, left: EExpression, right: EExpression): EExpression? {
        val leftBoolean = ConstantNormalizer.parseBooleanOrNull(left)
        val rightBoolean = ConstantNormalizer.parseBooleanOrNull(right)
        return when {
            leftBoolean == true || rightBoolean == true -> ConstantBuilder.buildBoolean(original, true)
            leftBoolean == false -> right
            rightBoolean == false -> left
            else -> null
        }
    }

    fun not(original: EExpression, expression: EConstantExpression): EConstantExpression {
        val boolean = ConstantNormalizer.parseBoolean(expression)
        return ConstantBuilder.buildBoolean(original, !boolean)
    }
}
