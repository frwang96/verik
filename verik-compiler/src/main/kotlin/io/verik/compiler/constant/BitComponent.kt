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

import java.math.BigInteger

class BitComponent(
    private val value: ByteArray,
    val width: Int
) {

    operator fun get(index: Int): Boolean {
        return if (index >= value.size * 8) {
            false
        } else {
            val byteIndex = value.size - (index / 8) - 1
            val bitIndex = index % 8
            value[byteIndex].toInt() and (1 shl bitIndex) != 0
        }
    }

    fun allZero(): Boolean {
        return value.all { it.toInt() == 0 }
    }

    fun toBigIntegerUnsigned(): BigInteger {
        return BigInteger(1, value)
    }

    companion object {

        operator fun invoke(bigInteger: BigInteger, width: Int): BitComponent {
            val value = bigInteger.toByteArray()
            for (byteIndex in value.indices) {
                for (bitIndex in 0 until 8) {
                    val index = (value.size - byteIndex - 1) * 8 + bitIndex
                    if (index >= width) {
                        value[byteIndex] = (value[byteIndex].toInt() and (1 shl bitIndex).inv()).toByte()
                    }
                }
            }
            return BitComponent(value, width)
        }

        fun zeroes(width: Int): BitComponent {
            val value = ByteArray(0)
            return BitComponent(value, width)
        }

        fun ones(width: Int): BitComponent {
            val value = ByteArray((width + 7) / 8)
            for (index in 0 until width) {
                val byteIndex = value.size - (index / 8) - 1
                val bitIndex = index % 8
                value[byteIndex] = (value[byteIndex].toInt() or (1 shl bitIndex)).toByte()
            }
            return BitComponent(value, width)
        }
    }
}
