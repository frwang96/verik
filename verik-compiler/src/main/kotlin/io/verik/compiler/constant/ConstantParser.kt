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
import io.verik.compiler.core.common.Core
import java.math.BigInteger

object ConstantParser {

    fun getInt(expression: EExpression): Int? {
        return if (expression is EConstantExpression && expression.type.reference == Core.Kt.C_Int) {
            expression.value.toInt()
        } else null
    }

    fun getBitConstant(expression: EExpression): BitConstant? {
        return if (expression is EConstantExpression &&
            expression.type.reference in listOf(Core.Vk.C_Ubit, Core.Vk.C_Sbit)
        ) {
            val width = expression.type.asBitWidth(expression)
            val signed = expression.type.asBitSigned(expression)
            val trimmedValue = expression.value.substringAfter("'").substring(if (signed) 2 else 1)
            val compactedValue = trimmedValue.replace("_", "")
            val bigInteger = BigInteger(compactedValue, 16)
            BitConstant(bigInteger, signed, width)
        } else null
    }
}
