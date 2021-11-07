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

import io.verik.compiler.ast.element.common.EExpression
import java.math.BigInteger

class BitConstant(val value: BigInteger, val width: Int) {

    constructor(value: Int, width: Int) : this(BigInteger.valueOf(value.toLong()), width)

    fun add(bitConstant: BitConstant, expression: EExpression): BitConstant {
        val resultWidth = expression.type.arguments[0].asCardinalValue(expression)
        val resultValue = truncate(this.value + bitConstant.value, resultWidth)
        return BitConstant(resultValue, resultWidth)
    }

    fun sub(bitConstant: BitConstant, expression: EExpression): BitConstant {
        val resultWidth = expression.type.arguments[0].asCardinalValue(expression)
        val resultValue = truncate(this.value - bitConstant.value, resultWidth)
        return BitConstant(resultValue, resultWidth)
    }

    override fun toString(): String {
        val valueString = value.toString(16)
        val valueStringLength = (width + 3) / 4
        val valueStringPadded = valueString.padStart(valueStringLength, '0')

        val builder = StringBuilder()
        builder.append("$width'h")
        valueStringPadded.forEachIndexed { index, it ->
            builder.append(it)
            val countToEnd = valueStringLength - index - 1
            if (countToEnd > 0 && countToEnd % 4 == 0)
                builder.append("_")
        }
        return builder.toString()
    }

    companion object {

        private fun truncate(value: BigInteger, width: Int): BigInteger {
            return value.mod(BigInteger.ONE.shiftLeft(width))
        }
    }
}
