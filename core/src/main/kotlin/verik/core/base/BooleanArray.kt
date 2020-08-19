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

class BooleanArray private constructor(
        val size: Int,
        private val intArray: IntArray
) {

    fun toInt(): Int {
        when {
            size > 33 -> throw IllegalArgumentException("count not convert boolean array to int")
            size == 33 -> if (get(32) != get(31)) {
                throw IllegalArgumentException("could not convert boolean array to int")
            }
        }
        var int = 0
        for (pos in 0 until 32) {
            val boolean = if (pos >= size) {
                get(size - 1)
            } else get(pos)
            if (boolean) {
                int = int or (1 shl pos)
            }
        }
        return int
    }

    operator fun get(index: Int): Boolean {
        if (index >= size) {
            throw IllegalArgumentException("index $index out of bounds")
        }
        val int = intArray[index / 32]
        return (int and (1 shl index)) != 0
    }

    override fun toString(): String {
        return (0 until size)
                .reversed()
                .joinToString(separator = "") { if(get(it)) "1" else "0" }
    }

    override fun equals(other: Any?): Boolean {
        return other is BooleanArray
                && other.size == size
                && other.intArray.contentEquals(intArray)
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + intArray.contentHashCode()
        return result
    }

    companion object {

        fun fromBoolean(x: Boolean): BooleanArray {
            val intArray = IntArray(1)
            intArray[0] = if (x) 1 else 0
            return BooleanArray(1, intArray)
        }

        fun fromIntImplicit(x: Int): BooleanArray {
            val size = if (x >= 0) {
                33 - x.countLeadingZeroBits()
            } else {
                33 - x.inv().countLeadingZeroBits()
            }
            val intArray = IntArray(1)
            intArray[0] = x and ((1 shl size) - 1)
            return BooleanArray(size, intArray)
        }

        fun fromIntExplicit(x: Int, size: Int): BooleanArray {
            return when {
                size > 32 -> throw IllegalArgumentException("illegal size of boolean array")
                size == 32 -> {
                    val intArray = IntArray(2)
                    intArray[0] = x
                    BooleanArray(size + 1, intArray)
                }
                else -> {
                    val intArray = IntArray(1)
                    intArray[0] = x and ((1 shl size) - 1)
                    BooleanArray(size + 1, intArray)
                }
            }
        }
    }
}