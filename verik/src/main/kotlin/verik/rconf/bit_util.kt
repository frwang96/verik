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

package verik.rconf

import verik.base.*

internal object _bit_util {

    fun get_hex_string(size: _int, value: _string): _string {
        return if (size == 0) "0"
        else {
            val boolean_array = get_boolean_array(size, value)
            val char_count = (size + 3) / 4
            val chars = CharArray(char_count)
            for (i in 0 until char_count) {
                var digit = 0
                for (j in 0 until 4) {
                    val pos = ((char_count - i - 1) * 4) + j
                    if (pos < boolean_array.size) {
                        if (boolean_array[pos]) digit = digit or (1 shl j)
                    }
                }
                chars[i] = get_hex_digit(digit)
            }
            return String(chars)
        }
    }

    private fun get_boolean_array(size: _int, value: _string): BooleanArray {
        val x = value.toInt()
        val boolean_array = BooleanArray(size)
        for (i in 0 until size) {
            boolean_array[i] = (x and (1 shl i)) != 0
        }
        return boolean_array
    }

    private fun get_hex_digit(digit: _int): Char {
        return when {
            digit < 10 -> '0' + digit
            digit < 16 -> 'a' + digit - 10
            else -> throw IllegalArgumentException("illegal hexadecimal digit")
        }
    }
}