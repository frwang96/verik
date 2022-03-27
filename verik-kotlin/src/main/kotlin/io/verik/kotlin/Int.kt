/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.kotlin

/**
 * Represents a 32-bit signed integer.
 */
class Int private constructor() {

    /**
     * Returns this value.
     */
    operator fun unaryPlus(): Int {
        throw VerikException()
    }

    /**
     * Returns the negative of this value.
     */
    operator fun unaryMinus(): Int {
        throw VerikException()
    }

    /**
     * Creates a range from this value to the specified [other] value.
     */
    operator fun rangeTo(other: Int): IntRange {
        throw VerikException()
    }

    /**
     * Adds the [other] value to this value.
     */
    operator fun plus(other: Int): Int {
        throw VerikException()
    }

    /**
     * Subtracts the [other] value from this value.
     */
    operator fun minus(other: Int): Int {
        throw VerikException()
    }

    /**
     * Multiplies this value by the [other] value.
     */
    operator fun times(other: Int): Int {
        throw VerikException()
    }

    /**
     * Shifts this value left by the [bitCount] number of bits.
     */
    infix fun shl(bitCount: Int): Int {
        throw VerikException()
    }

    /**
     * Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with copies of the sign bit.
     */
    infix fun shr(bitCount: Int): Int {
        throw VerikException()
    }

    /**
     * Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with zeros.
     */
    infix fun ushr(bitCount: Int): Int {
        throw VerikException()
    }
}
