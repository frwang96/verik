/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.kotlin

/**
 * Represents a value which is either true or false.
 */
class Boolean private constructor() {

    /**
     * Returns the inverse of this boolean.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Performs a logical and operation between this Boolean and the [other] one. Unlike the `&&` operator, this
     * function does not perform short-circuit evaluation. Both this and [other] will always be evaluated.
     */
    infix fun and(other: Boolean): Boolean {
        throw VerikException()
    }

    /**
     * Performs a logical or operation between this Boolean and the [other] one. Unlike the `||` operator, this function
     * does not perform short-circuit evaluation. Both this and [other] will always be evaluated.
     */
    infix fun or(other: Boolean): Boolean {
        throw VerikException()
    }

    /**
     * Performs a logical xor operation between this Boolean and the [other] one.
     */
    infix fun xor(other: Boolean): Boolean {
        throw VerikException()
    }
}
