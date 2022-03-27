/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.kotlin

/**
 * The String class represents character strings.
 */
class String private constructor() {

    /**
     * Returns a string obtained by concatenating this string with the string representation of [other].
     */
    operator fun plus(other: Any?): String {
        throw VerikException()
    }
}
