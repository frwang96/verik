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

class BitConstant(
    val value: BigInteger,
    val signed: Boolean,
    val width: Int
) {

    constructor(value: Int, signed: Boolean, width: Int) : this(BigInteger.valueOf(value.toLong()), signed, width)

    fun add(bitConstant: BitConstant, expression: EExpression): BitConstant {
        val resultWidth = expression.type.asBitWidth(expression)
        val resultSigned = expression.type.asBitSigned(expression)
        val resultValue = truncate(this.value + bitConstant.value, resultWidth)
        return BitConstant(resultValue, resultSigned, resultWidth)
    }

    fun sub(bitConstant: BitConstant, expression: EExpression): BitConstant {
        val resultWidth = expression.type.asBitWidth(expression)
        val resultSigned = expression.type.asBitSigned(expression)
        val resultValue = truncate(this.value - bitConstant.value, resultWidth)
        return BitConstant(resultValue, resultSigned, resultWidth)
    }

    companion object {

        private fun truncate(value: BigInteger, width: Int): BigInteger {
            return value.mod(BigInteger.ONE.shiftLeft(width))
        }
    }
}
