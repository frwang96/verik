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
import kotlin.experimental.and
import kotlin.experimental.inv

class BitComponent(
    private val value: ByteArray,
    val width: Int
) {

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
                        value[byteIndex] = value[byteIndex] and (1 shl bitIndex).toByte().inv()
                    }
                }
            }
            return BitComponent(value, width)
        }
    }
}
