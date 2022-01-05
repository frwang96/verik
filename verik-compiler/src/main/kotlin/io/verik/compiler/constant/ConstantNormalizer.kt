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
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import java.math.BigInteger

object ConstantNormalizer {

    fun normalizeBoolean(constantExpression: EConstantExpression): EConstantExpression {
        val boolean = when (val value = constantExpression.value) {
            "false" -> false
            "true" -> true
            else -> {
                Messages.INTERNAL_ERROR.on(SourceLocation.NULL, "Unrecognized boolean value: $value")
            }
        }
        return EConstantExpression(
            constantExpression.location,
            constantExpression.type,
            ConstantFormatter.formatBoolean(boolean)
        )
    }

    fun normalizeInt(constantExpression: EConstantExpression): EConstantExpression {
        val value = constantExpression.value.replace("_", "")
        val int = when {
            value.startsWith("0x") || value.startsWith("0X") -> value.substring(2).toInt(16)
            value.startsWith("0b") || value.startsWith("0B") -> value.substring(2).toInt(2)
            else -> value.toInt()
        }
        return EConstantExpression(
            constantExpression.location,
            constantExpression.type,
            ConstantFormatter.formatInt(int)
        )
    }

    fun normalizeBitConstant(expression: EExpression, signed: Boolean): EConstantExpression? {
        val bitConstant = when (expression) {
            is EConstantExpression -> {
                normalizeBitConstantInt(expression.value, signed)
            }
            is EStringTemplateExpression -> {
                if (expression.entries.size != 1) {
                    Messages.ILLEGAL_BIT_CONSTANT.on(expression)
                    null
                } else {
                    val stringEntry = expression.entries[0]
                    if (stringEntry is LiteralStringEntry) {
                        normalizeBitConstantString(stringEntry.text, signed, expression)
                    } else {
                        Messages.ILLEGAL_BIT_CONSTANT.on(expression)
                        null
                    }
                }
            }
            else -> {
                Messages.ILLEGAL_BIT_CONSTANT.on(expression)
                null
            }
        } ?: return null
        val type = if (signed) Core.Vk.C_Sbit.toType(Cardinal.of(bitConstant.width).toType())
        else Core.Vk.C_Ubit.toType(Cardinal.of(bitConstant.width).toType())
        return EConstantExpression(
            expression.location,
            type,
            ConstantFormatter.formatBitConstant(bitConstant)
        )
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
            Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
            return null
        }
        val tickIndex = compactedValue.indexOf('\'')
        val width = compactedValue.substring(0, tickIndex).toIntOrNull()
        if (width == null) {
            Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
            return null
        }

        val trimmedValue = compactedValue.substring(tickIndex + 2)
        val bitConstant = when (compactedValue[tickIndex + 1].lowercase()) {
            "d" -> {
                try {
                    val bigInteger = BigInteger(trimmedValue, 10)
                    BitConstant(bigInteger, signed, width)
                } catch (exception: NumberFormatException) {
                    Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                    null
                }
            }
            "h" -> {
                try {
                    val bigInteger = BigInteger(trimmedValue, 16)
                    if (signed && bigInteger.testBit(width - 1)) {
                        BitConstant(bigInteger - BigInteger.ONE.shiftLeft(width), true, width)
                    } else {
                        BitConstant(bigInteger, signed, width)
                    }
                } catch (exception: NumberFormatException) {
                    Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                    null
                }
            }
            "b" -> {
                try {
                    val bigInteger = BigInteger(trimmedValue, 2)
                    if (signed && bigInteger.testBit(width - 1)) {
                        BitConstant(bigInteger - BigInteger.ONE.shiftLeft(width), true, width)
                    } else {
                        BitConstant(bigInteger, signed, width)
                    }
                } catch (exception: java.lang.NumberFormatException) {
                    Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                    null
                }
            }
            else -> {
                Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                null
            }
        }
        if (bitConstant != null && !bitConstant.isInRange()) {
            Messages.BIT_CONSTANT_INSUFFICIENT_WIDTH.on(element, value)
        }
        return bitConstant
    }
}
