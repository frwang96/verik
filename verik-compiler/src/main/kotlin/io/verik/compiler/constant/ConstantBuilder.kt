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

import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

object ConstantBuilder {

    fun buildNull(location: SourceLocation): EConstantExpression {
        return EConstantExpression(location, Core.Kt.C_Nothing.toType(), "null")
    }

    fun buildBoolean(location: SourceLocation, kind: BooleanConstantKind): EConstantExpression {
        return EConstantExpression(
            location,
            Core.Kt.C_Boolean.toType(),
            formatBoolean(kind)
        )
    }

    fun buildBoolean(original: EExpression, kind: BooleanConstantKind): EConstantExpression {
        return buildBoolean(original.location, kind)
    }

    fun buildInt(location: SourceLocation, int: Int): EConstantExpression {
        return EConstantExpression(location, Core.Kt.C_Int.toType(), formatInt(int))
    }

    fun buildInt(original: EExpression, int: Int): EConstantExpression {
        return buildInt(original.location, int)
    }

    fun buildBitConstant(location: SourceLocation, bitConstant: BitConstant): EConstantExpression {
        return EConstantExpression(location, bitConstant.getType(), formatBitConstant(bitConstant))
    }

    fun buildBitConstant(location: SourceLocation, width: Int, value: Int): EConstantExpression {
        val bitConstant = BitConstant(value, false, width)
        return buildBitConstant(location, bitConstant)
    }

    fun buildBitConstant(original: EExpression, bitConstant: BitConstant): EConstantExpression {
        return buildBitConstant(original.location, bitConstant)
    }

    private fun formatBoolean(kind: BooleanConstantKind): String {
        return when (kind) {
            BooleanConstantKind.TRUE -> "1'b1"
            BooleanConstantKind.FALSE -> "1'b0"
            BooleanConstantKind.UNKNOWN -> "1'bx"
            BooleanConstantKind.FLOATING -> "1'bz"
        }
    }

    private fun formatInt(int: Int): String {
        return int.toString()
    }

    private fun formatBitConstant(bitConstant: BitConstant): String {
        if (bitConstant.kind.allOnes()) {
            if (bitConstant.value.allZeroes())
                return if (bitConstant.signed) "${bitConstant.width}'sbx" else "${bitConstant.width}'bx"
            if (bitConstant.value.allOnes())
                return if (bitConstant.signed) "${bitConstant.width}'sbz" else "${bitConstant.width}'bz"
        }
        return if (bitConstant.width < 8 || !bitConstant.kind.allZeroes()) {
            formatBinBitConstant(bitConstant)
        } else {
            formatHexBitConstant(bitConstant)
        }
    }

    private fun formatHexBitConstant(bitConstant: BitConstant): String {
        val valueString = bitConstant.value.toBigIntegerUnsigned().toString(16)
        val valueStringLength = (bitConstant.width + 3) / 4
        val valueStringPadded = valueString.padStart(valueStringLength, '0')

        val builder = StringBuilder()
        builder.append("${bitConstant.width}")
        if (bitConstant.signed) builder.append("'sh")
        else builder.append("'h")
        valueStringPadded.indices.forEach {
            val reverseIndex = (valueStringLength - it - 1)
            val kind = bitConstant.kind[reverseIndex * 4]
            val value = bitConstant.value[reverseIndex * 4]
            when {
                kind && !value -> builder.append("x")
                kind && value -> builder.append("z")
                else -> builder.append(valueStringPadded[it])
            }
            if (reverseIndex > 0 && reverseIndex % 4 == 0) {
                builder.append("_")
            }
        }
        return builder.toString()
    }

    private fun formatBinBitConstant(bitConstant: BitConstant): String {
        val builder = StringBuilder()
        builder.append("${bitConstant.width}")
        if (bitConstant.signed) {
            builder.append("'sb")
        } else builder.append("'b")
        (0 until bitConstant.width).reversed().forEach {
            val kind = bitConstant.kind[it]
            val value = bitConstant.value[it]
            when {
                !kind && !value -> builder.append("0")
                !kind && value -> builder.append("1")
                kind && !value -> builder.append("x")
                else -> builder.append("z")
            }
            if (it > 0 && it % 4 == 0) {
                builder.append("_")
            }
        }
        return builder.toString()
    }
}
