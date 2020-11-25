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

package verik.data

internal fun get_bits(size: Int, value: Int): BooleanArray {
    val bits = BooleanArray(size)
    for (i in 0 until size) {
        bits[i] = (value and (1 shl i)) != 0
    }
    return bits
}

internal fun get_hex_string(size: Int, bits: BooleanArray): String {
    return if (size == 0) "0"
    else {
        val char_count = (size + 3) / 4
        val chars = CharArray(char_count)
        for (i in 0 until char_count) {
            var digit = 0
            for (j in 0 until 4) {
                val pos = ((char_count - i - 1) * 4) + j
                if (pos < bits.size) {
                    if (bits[pos]) digit = digit or (1 shl j)
                }
            }
            chars[i] = get_hex_digit(digit)
        }
        return String(chars)
    }
}

internal fun get_hex_digit(digit: Int): Char {
    return when {
        digit < 10 -> '0' + digit
        digit < 16 -> 'a' + digit - 10
        else -> throw IllegalArgumentException("illegal hexadecimal digit")
    }
}