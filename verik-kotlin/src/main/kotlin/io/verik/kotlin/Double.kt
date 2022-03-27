/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.kotlin

/**
 * Represents a double-precision floating point number.
 */
class Double private constructor() {

    /**
     * Adds the [other] value to this value.
     */
    operator fun plus(other: Int): Double {
        throw VerikException()
    }

    /**
     * Adds the [other] value to this value.
     */
    operator fun plus(other: Double): Double {
        throw VerikException()
    }

    /**
     * Divides this value by the [other] value.
     */
    operator fun div(other: Int): Double {
        throw VerikException()
    }
}
