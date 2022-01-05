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
import io.verik.compiler.message.Messages
import java.math.BigInteger

object ConstantParser {

    fun parseInt(expression: EConstantExpression): Int {
        return expression.value.toInt()
    }

    fun parseBitConstant(expression: EConstantExpression): BitConstant {
        val tickIndex = expression.value.indexOf("'")
        val width = expression.value.substring(0, tickIndex).toIntOrNull()
            ?: Messages.INTERNAL_ERROR.on(expression, "Unable to parse bit constant: ${expression.value}")
        val signed = (expression.value[tickIndex + 1] == 's')
        val trimmedValue = expression.value.substring(tickIndex).substring(if (signed) 3 else 2)
        val compactedValue = trimmedValue.replace("_", "")
        val bigInteger = BigInteger(compactedValue, 16)
        return BitConstant(bigInteger, signed, width)
    }
}
