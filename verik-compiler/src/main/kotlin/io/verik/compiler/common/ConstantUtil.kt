/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.core.common.Core

object ConstantUtil {

    fun normalizeGetIntValue(value: String): Int {
        val trimmedValue = value.replace("_", "")
        return when {
            value.startsWith("0x") || value.startsWith("0X") ->
                trimmedValue.substring(2).toInt(16)
            value.startsWith("0b") || value.startsWith("0B") ->
                trimmedValue.substring(2).toInt(2)
            else -> trimmedValue.toInt()
        }
    }

    fun normalizeGetIntWidth(value: String): Int {
        val trimmedValue = value.replace("_", "")
        return when {
            value.startsWith("0x") || value.startsWith("0X") ->
                trimmedValue.substring(2).length * 4
            value.startsWith("0b") || value.startsWith("0B") ->
                trimmedValue.substring(2).length
            else -> {
                val valueInt = trimmedValue.toInt()
                if (valueInt == 0) 1
                else 32 - valueInt.countLeadingZeroBits()
            }
        }
    }

    fun getInt(expression: EExpression): Int? {
        return if (expression is EConstantExpression && expression.type.reference == Core.Kt.C_INT) {
            expression.value.toInt()
        } else null
    }
}
