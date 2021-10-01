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
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import java.math.BigInteger

object ConstantUtil {

    fun getBoolean(expression: EExpression): Boolean? {
        return if (expression is EConstantExpression && expression.type.reference == Core.Kt.C_Boolean) {
            when (val value = expression.value) {
                "false", "1'b0" -> false
                "true", "1'b1" -> true
                else -> {
                    Messages.INTERNAL_ERROR.on(expression, "Unrecognized boolean value: $value")
                    false
                }
            }
        } else null
    }

    fun getInt(expression: EExpression): Int? {
        return if (expression is EConstantExpression && expression.type.reference == Core.Kt.C_Int) {
            val compactedValue = expression.value.replace("_", "")
            return when {
                compactedValue.startsWith("0x") || compactedValue.startsWith("0X") ->
                    compactedValue.substring(2).toInt(16)
                compactedValue.startsWith("0b") || compactedValue.startsWith("0B") ->
                    compactedValue.substring(2).toInt(2)
                else ->
                    compactedValue.toInt()
            }
        } else null
    }

    fun getBitConstant(expression: EExpression): BitConstant? {
        return when (expression) {
            is EConstantExpression -> {
                if (expression.type.reference == Core.Kt.C_Int) {
                    getBitConstantInt(expression.value)
                } else null
            }
            is EStringTemplateExpression -> {
                if (expression.entries.size != 1) {
                    null
                } else {
                    val stringEntry = expression.entries[0]
                    if (stringEntry is LiteralStringEntry) {
                        getBitConstantString(stringEntry.text, expression)
                    } else null
                }
            }
            else -> null
        }
    }

    private fun getBitConstantInt(value: String): BitConstant {
        val compactedValue = value.replace("_", "")
        return when {
            compactedValue.startsWith("0x") || compactedValue.startsWith("0X") -> {
                val trimmedValue = compactedValue.substring(2)
                val valueInt = trimmedValue.toInt(16)
                val width = trimmedValue.length * 4
                BitConstant(valueInt, width)
            }
            compactedValue.startsWith("0b") || compactedValue.startsWith("0B") -> {
                val trimmedValue = compactedValue.substring(2)
                val valueInt = trimmedValue.toInt(2)
                val width = trimmedValue.length
                BitConstant(valueInt, width)
            }
            else -> {
                val valueInt = compactedValue.toInt()
                val width = if (valueInt == 0) 1
                else 32 - valueInt.countLeadingZeroBits()
                BitConstant(valueInt, width)
            }
        }
    }

    private fun getBitConstantString(value: String, element: EElement): BitConstant {
        val compactedValue = value.replace("_", "")
        val tickCount = compactedValue.count { it == '\'' }
        if (tickCount != 1) {
            Messages.BIT_CONSTANT_ERROR.on(element, value)
            return BitConstant(0, 1)
        }
        val tickIndex = compactedValue.indexOf('\'')
        val width = compactedValue.substring(0, tickIndex).toIntOrNull()
        if (width == null) {
            Messages.BIT_CONSTANT_ERROR.on(element, value)
            return BitConstant(0, 1)
        }
        val trimmedValue = compactedValue.substring(tickIndex + 2)
        return when (compactedValue[tickIndex + 1]) {
            'h', 'H' -> {
                val bigInteger = BigInteger(trimmedValue, 16)
                BitConstant(bigInteger, width)
            }
            'b', 'B' -> {
                val bigInteger = BigInteger(trimmedValue, 2)
                BitConstant(bigInteger, width)
            }
            else -> {
                Messages.BIT_CONSTANT_ERROR.on(element, value)
                BitConstant(0, 1)
            }
        }
    }

    fun formatBoolean(boolean: Boolean): String {
        return when (boolean) {
            true -> "1'b1"
            false -> "1'b0"
        }
    }

    fun formatInt(int: Int): String {
        return int.toString()
    }

    fun formatBitConstant(bitConstant: BitConstant): String {
        val valueString = bitConstant.value.toString(16)
        val valueStringLength = (bitConstant.width + 3) / 4
        val valueStringPadded = valueString.padStart(valueStringLength, '0')

        val builder = StringBuilder()
        builder.append("${bitConstant.width}'h")
        valueStringPadded.forEachIndexed { index, it ->
            builder.append(it)
            val countToEnd = valueStringLength - index - 1
            if (countToEnd > 0 && countToEnd % 4 == 0)
                builder.append("_")
        }
        return builder.toString()
    }
}
