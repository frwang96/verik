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

package verik.core.base

class BitArray private constructor(
        val size: Int,
        private val intArray: IntArray
) {

    operator fun get(index: Int): Boolean {
        return if (index >= size) false
        else {
            val int = intArray[index / 32]
            return (int and (1 shl index)) != 0
        }
    }

    companion object {

        operator fun invoke(x: Int, size: Int): BitArray {
            if (size > 32) {
                throw IllegalArgumentException("illegal size of bit array")
            }
            val intArray = IntArray(1)
            intArray[0] = x and ((1 shl size) - 1)
            return BitArray(size, intArray)
        }

        operator fun invoke(x: Boolean): BitArray {
            val intArray = IntArray(1)
            intArray[0] = if (x) 1 else 0
            return BitArray(1, intArray)
        }
    }
}