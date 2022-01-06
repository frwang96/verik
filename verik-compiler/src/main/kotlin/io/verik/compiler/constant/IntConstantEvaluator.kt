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

object IntConstantEvaluator {

    fun plusInt(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftInt = ConstantNormalizer.parseInt(left)
        val rightInt = ConstantNormalizer.parseInt(right)
        return ConstantBuilder.buildInt(original, leftInt + rightInt)
    }

    fun minusInt(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftInt = ConstantNormalizer.parseInt(left)
        val rightInt = ConstantNormalizer.parseInt(right)
        return ConstantBuilder.buildInt(original, leftInt - rightInt)
    }

    fun timesInt(original: EExpression, left: EConstantExpression, right: EConstantExpression): EConstantExpression {
        val leftInt = ConstantNormalizer.parseInt(left)
        val rightInt = ConstantNormalizer.parseInt(right)
        return ConstantBuilder.buildInt(original, leftInt * rightInt)
    }
}
