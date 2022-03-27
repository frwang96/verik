/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.kotlin

/**
 * A range of values of type [Int].
 */
class IntRange private constructor()

/**
 * Returns a range from this value up to but excluding the specified [to] value.
 */
infix fun Int.until(to: Int): IntRange {
    throw VerikException()
}
