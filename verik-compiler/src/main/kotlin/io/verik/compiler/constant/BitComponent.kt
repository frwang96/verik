/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

import java.math.BigInteger

class BitComponent(
    private val value: ByteArray,
    val width: Int
) {

    operator fun get(index: Int): Boolean {
        return if (index >= width) {
            throw IllegalArgumentException("Bit component index out of range: $index")
        } else {
            val byteIndex = value.size - (index / 8) - 1
            val bitIndex = index % 8
            value[byteIndex].toInt() and (1 shl bitIndex) != 0
        }
    }

    fun allZeroes(): Boolean {
        return (0 until width).all { !get(it) }
    }

    fun allOnes(): Boolean {
        return (0 until width).all { get(it) }
    }

    fun toBigIntegerUnsigned(): BigInteger {
        return BigInteger(1, value)
    }

    companion object {

        operator fun invoke(bigInteger: BigInteger, width: Int): BitComponent {
            val value = ByteArray(width + 7 / 8)
            for (byteIndex in value.indices) {
                for (bitIndex in 0 until 8) {
                    val index = (value.size - byteIndex - 1) * 8 + bitIndex
                    if (bigInteger.testBit(index)) {
                        value[byteIndex] = (value[byteIndex].toInt() or (1 shl bitIndex)).toByte()
                    }
                }
            }
            return BitComponent(value, width)
        }

        fun zeroes(width: Int): BitComponent {
            val value = ByteArray(width + 7 / 8)
            return BitComponent(value, width)
        }

        fun ones(width: Int): BitComponent {
            val value = ByteArray(width + 7 / 8)
            for (index in 0 until width) {
                val byteIndex = value.size - (index / 8) - 1
                val bitIndex = index % 8
                value[byteIndex] = (value[byteIndex].toInt() or (1 shl bitIndex)).toByte()
            }
            return BitComponent(value, width)
        }
    }
}
