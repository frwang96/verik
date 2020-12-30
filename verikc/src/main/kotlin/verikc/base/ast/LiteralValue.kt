/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.base.ast

import kotlin.math.max

class LiteralValue private constructor(
        val width: Int,
        private val intArray: IntArray
) {

    fun toBoolean(): Boolean {
        if (width != 1) throw IllegalArgumentException("could not convert literal value to Boolean")
        return (intArray[0] and 1) != 0
    }

    fun toInt(): Int {
        if (width != 32) throw IllegalArgumentException("could not convert literal value to Int")
        return intArray[0]
    }

    fun hexString(): String {
        val length = max((width + 3) / 4, 1)
        val builder = StringBuilder()
        for (charPos in (length - 1) downTo 0) {
            builder.append(hexChar(charPos))
            if (charPos != 0 && (charPos % 4) == 0) {
                builder.append('_')
            }
        }
        return builder.toString()
    }

    override fun toString(): String {
        return "$width'h${hexString()}"
    }

    override fun equals(other: Any?): Boolean {
        return other is LiteralValue
                && other.width == width
                && other.intArray.contentEquals(intArray)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + intArray.contentHashCode()
        return result
    }

    private fun hexChar(charPos: Int): Char {
        var code = 0
        for (index in 0 until 4) {
            val bitPos = (charPos * 4) + index
            val bit = if (bitPos >= width) false
            else (intArray[bitPos / 32] and (1 shl (bitPos % 32))) != 0
            if (bit) {
                code = code or (1 shl index)
            }
        }
        return when {
            code < 10 -> '0' + code
            code < 16 -> 'a' + code - 10
            else -> throw IllegalArgumentException("char code out of range")
        }
    }

    companion object {

        fun fromBoolean(x: Boolean): LiteralValue {
            val intArray = IntArray(1)
            intArray[0] = if (x) 1 else 0
            return LiteralValue(1, intArray)
        }

        fun fromInt(x: Int): LiteralValue {
            val intArray = IntArray(1)
            intArray[0] = x
            return LiteralValue(32, intArray)
        }

        fun fromBitInt(width: Int, x: Int, line: Line): LiteralValue {
            if (width <= 0) throw LineException("illegal width $width", line)
            val effectiveWidth = if (x >= 0) {
                32 - x.countLeadingZeroBits()
            } else {
                32 - x.inv().countLeadingZeroBits()
            }
            if (effectiveWidth > width) throw LineException("unable to cast int literal $x to width $width", line)
            val intArray = IntArray((width + 31) / 32)
            intArray[0] = x
            return LiteralValue(width, intArray)
        }
    }
}
