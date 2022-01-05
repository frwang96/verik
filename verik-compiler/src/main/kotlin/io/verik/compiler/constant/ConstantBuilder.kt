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
import io.verik.compiler.message.SourceLocation

object ConstantBuilder {

    fun buildBoolean(original: EExpression, boolean: Boolean): EConstantExpression {
        return EConstantExpression(
            original.location,
            Core.Kt.C_Boolean.toType(),
            formatBoolean(boolean)
        )
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

    private fun formatBoolean(boolean: Boolean): String {
        return when (boolean) {
            true -> "1'b1"
            false -> "1'b0"
        }
    }

    private fun formatInt(int: Int): String {
        return int.toString()
    }

    private fun formatBitConstant(bitConstant: BitConstant): String {
        val valueString = bitConstant.getModValue().toString(16)
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
