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

object ConstantFormatter {

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
