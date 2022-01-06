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
        return ConstantBuilder.buildBoolean(constantExpression, boolean)
    }

    fun normalizeInt(constantExpression: EConstantExpression): EConstantExpression {
        val value = constantExpression.value.replace("_", "")
        val int = when {
            value.startsWith("0x") || value.startsWith("0X") -> value.substring(2).toInt(16)
            value.startsWith("0b") || value.startsWith("0B") -> value.substring(2).toInt(2)
            else -> value.toInt()
        }
        return ConstantBuilder.buildInt(constantExpression, int)
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
        return ConstantBuilder.buildBitConstant(expression.location, bitConstant)
    }

    fun parseInt(constantExpression: EConstantExpression): Int {
        return constantExpression.value.toInt()
    }

    fun parseBitConstant(constantExpression: EConstantExpression): BitConstant {
        val width = constantExpression.type.asBitWidth(constantExpression)
        val signed = constantExpression.type.asBitSigned(constantExpression)
        return normalizeBitConstantString(constantExpression.value, signed, constantExpression)
            ?: BitConstant(0, signed, width)
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
        return when (compactedValue[tickIndex + 1].lowercase()) {
            "d" -> parseDecBitConstant(value, width, signed, trimmedValue, element)
            "h" -> parseHexBitConstant(value, width, signed, trimmedValue, element)
            "b" -> parseBinBitConstant(value, width, signed, trimmedValue, element)
            else -> {
                Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                null
            }
        }
    }

    private fun parseDecBitConstant(
        value: String,
        width: Int,
        signed: Boolean,
        trimmedValue: String,
        element: EElement,
    ): BitConstant? {
        return try {
            val bigInteger = BigInteger(trimmedValue)
            if (bigInteger >= BigInteger.ONE.shiftLeft(width)) {
                Messages.BIT_CONSTANT_INSUFFICIENT_WIDTH.on(element, value)
                return null
            }
            BitConstant(BitComponent.zeroes(width), BitComponent(bigInteger, width), signed, width)
        } catch (exception: NumberFormatException) {
            Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
            null
        }
    }

    private fun parseHexBitConstant(
        value: String,
        width: Int,
        signed: Boolean,
        trimmedValue: String,
        element: EElement,
    ): BitConstant? {
        if ((width + 3) / 4 < trimmedValue.length) {
            Messages.BIT_CONSTANT_INSUFFICIENT_WIDTH.on(element, value)
            return null
        }
        val kindByteArray = ByteArray(width + 7 / 8)
        val valueByteArray = ByteArray(width + 7 / 8)
        trimmedValue.forEachIndexed { charIndex, char ->
            val charKind = char in listOf('x', 'X', 'z', 'Z')
            val charValue = when (char) {
                in '0'..'9' -> char - '0'
                in 'a'..'f' -> char - 'a' + 10
                in 'A'..'F' -> char - 'A' + 10
                'x', 'X' -> 0
                'z', 'Z' -> 1
                else -> {
                    Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                    return null
                }
            }
            for (charBitIndex in 0 until 4) {
                val index = (trimmedValue.length - charIndex - 1) * 4 + charBitIndex
                if (index >= width)
                    break
                val byteIndex = valueByteArray.size - (index / 8) - 1
                val byteBitIndex = index % 8
                if (charKind) {
                    kindByteArray[byteIndex] = (kindByteArray[byteIndex].toInt() or (1 shl byteBitIndex)).toByte()
                    if (charValue == 1) {
                        valueByteArray[byteIndex] = (valueByteArray[byteIndex].toInt() or (1 shl byteBitIndex)).toByte()
                    }
                } else if (charValue and (1 shl charBitIndex) != 0) {
                    valueByteArray[byteIndex] = (valueByteArray[byteIndex].toInt() or (1 shl byteBitIndex)).toByte()
                }
            }
        }
        if (trimmedValue.length < (width + 3) / 4) {
            extend(kindByteArray, valueByteArray, trimmedValue.length * 4, width, signed)
        }
        return BitConstant(
            BitComponent(kindByteArray, width),
            BitComponent(valueByteArray, width),
            signed,
            width
        )
    }

    private fun parseBinBitConstant(
        value: String,
        width: Int,
        signed: Boolean,
        trimmedValue: String,
        element: EElement,
    ): BitConstant? {
        if (width < trimmedValue.length) {
            Messages.BIT_CONSTANT_INSUFFICIENT_WIDTH.on(element, value)
            return null
        }
        val kindByteArray = ByteArray(width + 7 / 8)
        val valueByteArray = ByteArray(width + 7 / 8)
        trimmedValue.reversed().forEachIndexed { index, char ->
            val charKind = char in listOf('x', 'X', 'z', 'Z')
            val charValue = when (char) {
                '0' -> false
                '1' -> true
                'x', 'X' -> false
                'z', 'Z' -> true
                else -> {
                    Messages.BIT_CONSTANT_PARSE_ERROR.on(element, value)
                    return null
                }
            }
            val byteIndex = valueByteArray.size - (index / 8) - 1
            val byteBitIndex = index % 8
            if (charKind)
                kindByteArray[byteIndex] = (kindByteArray[byteIndex].toInt() or (1 shl byteBitIndex)).toByte()
            if (charValue)
                valueByteArray[byteIndex] = (valueByteArray[byteIndex].toInt() or (1 shl byteBitIndex)).toByte()
        }
        if (trimmedValue.length < width) {
            extend(kindByteArray, valueByteArray, trimmedValue.length, width, signed)
        }
        return BitConstant(
            BitComponent(kindByteArray, width),
            BitComponent(valueByteArray, width),
            signed,
            width
        )
    }

    private fun extend(
        kindByteArray: ByteArray,
        valueByteArray: ByteArray,
        fromWidth: Int,
        toWidth: Int,
        signed: Boolean
    ) {
        val leadingByteIndex = kindByteArray.size - ((fromWidth - 1) / 8) - 1
        val leadingByteBitIndex = (fromWidth - 1) % 8
        val leadingKind = (kindByteArray[leadingByteIndex].toInt() and (1 shl leadingByteBitIndex)) != 0
        val leadingValue = (valueByteArray[leadingByteIndex].toInt() and (1 shl leadingByteBitIndex)) != 0
        val extendValue = if (!leadingKind && !signed) false else leadingValue
        (fromWidth until toWidth).forEach {
            val byteIndex = kindByteArray.size - (it / 8) - 1
            val byteBitIndex = it % 8
            if (leadingKind)
                kindByteArray[byteIndex] = ((1 shl byteBitIndex) or kindByteArray[byteIndex].toInt()).toByte()
            if (extendValue)
                valueByteArray[byteIndex] = ((1 shl byteBitIndex) or valueByteArray[byteIndex].toInt()).toByte()
        }
    }
}
