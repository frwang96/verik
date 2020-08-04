/*
 * Copyright 2020 Francis Wang
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

package io.verik.common.data

import java.util.*

internal fun getBits(len: Int, value: Int): BitSet {
    val bits = BitSet(len)
    for (i in 0 until len) {
        bits.set(i, (value and (1 shl i)) != 0)
    }
    return bits
}

internal fun getHexString(len: Int, bits: BitSet): String {
    return if (len == 0) "0x0"
    else {
        val charCount = (len + 3) / 4
        val chars = CharArray(charCount)
        for (i in 0 until charCount) {
            var digit = 0
            for (j in 0 until 4) {
                val pos = ((charCount - i - 1) * 4) + j
                if (bits.get(pos)) digit = digit or (1 shl j)
            }
            chars[i] = getHexDigit(digit)
        }
        return "0x${String(chars)}"
    }
}

internal fun getHexDigit(digit: Int): Char {
    return when {
        digit < 10 -> '0' + digit
        digit < 16 -> 'a' + digit - 10
        else -> throw IllegalArgumentException("illegal hexadecimal digit")
    }
}