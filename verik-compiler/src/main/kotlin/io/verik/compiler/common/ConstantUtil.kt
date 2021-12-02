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
import io.verik.compiler.message.SourceLocation
import java.math.BigInteger

object ConstantUtil {

    fun normalizeBoolean(value: String): Boolean {
        return when (value) {
            "false" -> false
            "true" -> true
            else -> Messages.INTERNAL_ERROR.on(SourceLocation.NULL, "Unrecognized boolean value: $value")
        }
    }

    fun normalizeInt(value: String): Int {
        val compactedValue = value.replace("_", "")
        return when {
            compactedValue.startsWith("0x") || compactedValue.startsWith("0X") ->
                compactedValue.substring(2).toInt(16)
            compactedValue.startsWith("0b") || compactedValue.startsWith("0B") ->
                compactedValue.substring(2).toInt(2)
            else -> compactedValue.toInt()
        }
    }

    fun normalizeBitConstant(expression: EExpression, signed: Boolean): BitConstant? {
        return when (expression) {
            is EConstantExpression -> {
                if (expression.type.reference == Core.Kt.C_Int) {
                    normalizeBitConstantInt(expression.value, signed)
                } else {
                    Messages.INTERNAL_ERROR.on(expression, "Unrecognized constant expression type: ${expression.type}")
                }
            }
            is EStringTemplateExpression -> {
                if (expression.entries.size != 1) {
                    Messages.BIT_CONSTANT_NOT_LITERAL.on(expression)
                    null
                } else {
                    val stringEntry = expression.entries[0]
                    if (stringEntry is LiteralStringEntry) {
                        normalizeBitConstantString(stringEntry.text, signed, expression)
                    } else {
                        Messages.BIT_CONSTANT_NOT_LITERAL.on(expression)
                        null
                    }
                }
            }
            else -> {
                Messages.BIT_CONSTANT_NOT_LITERAL.on(expression)
                null
            }
        }
    }

    private fun normalizeBitConstantInt(value: String, signed: Boolean): BitConstant {
        val compactedValue = value.replace("_", "")
        return when {
            compactedValue.startsWith("0x") || compactedValue.startsWith("0X") -> {
                val trimmedValue = compactedValue.substring(2)
                val valueInt = trimmedValue.toInt(16)
                val width = trimmedValue.length * 4
                BitConstant(valueInt, signed, width)
            }
            compactedValue.startsWith("0b") || compactedValue.startsWith("0B") -> {
                val trimmedValue = compactedValue.substring(2)
                val valueInt = trimmedValue.toInt(2)
                val width = trimmedValue.length
                BitConstant(valueInt, signed, width)
            }
            else -> {
                val valueInt = compactedValue.toInt()
                val width = if (valueInt == 0) 1
                else 32 - valueInt.countLeadingZeroBits()
                BitConstant(valueInt, signed, width)
            }
        }
    }

    private fun normalizeBitConstantString(value: String, signed: Boolean, element: EElement): BitConstant? {
        val compactedValue = value.replace("_", "")
        val tickCount = compactedValue.count { it == '\'' }
        if (tickCount != 1) {
            Messages.BIT_CONSTANT_ERROR.on(element, value)
            return null
        }
        val tickIndex = compactedValue.indexOf('\'')
        val width = compactedValue.substring(0, tickIndex).toIntOrNull()
        if (width == null) {
            Messages.BIT_CONSTANT_ERROR.on(element, value)
            return null
        }

        val trimmedValue = compactedValue.substring(tickIndex + 2)
        return when (compactedValue[tickIndex + 1].toLowerCase()) {
            'h' -> {
                try {
                    val bigInteger = BigInteger(trimmedValue, 16)
                    BitConstant(bigInteger, signed, width)
                } catch (exception: NumberFormatException) {
                    Messages.BIT_CONSTANT_ERROR.on(element, value)
                    null
                }
            }
            'b' -> {
                try {
                    val bigInteger = BigInteger(trimmedValue, 2)
                    BitConstant(bigInteger, signed, width)
                } catch (exception: java.lang.NumberFormatException) {
                    Messages.BIT_CONSTANT_ERROR.on(element, value)
                    null
                }
            }
            else -> {
                Messages.BIT_CONSTANT_ERROR.on(element, value)
                null
            }
        }
    }

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
        builder.append("${bitConstant.width}")
        if (bitConstant.signed) builder.append("'sh")
        else builder.append("'h")
        valueStringPadded.forEachIndexed { index, it ->
            builder.append(it)
            val countToEnd = valueStringLength - index - 1
            if (countToEnd > 0 && countToEnd % 4 == 0)
                builder.append("_")
        }
        return builder.toString()
    }
}
