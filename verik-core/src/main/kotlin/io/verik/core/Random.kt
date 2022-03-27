/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Returns a random integer.
 */
fun random(): Int {
    throw VerikException()
}

/**
 * Returns a random integer from 0 to [max] exclusive.
 */
fun random(max: Int): Int {
    throw VerikException()
}

/**
 * Returns a random integer from [min] inclusive to [max] exclusive.
 */
fun random(min: Int, max: Int): Int {
    throw VerikException()
}

/**
 * Returns a random [Boolean].
 */
fun randomBoolean(): Boolean {
    throw VerikException()
}

/**
 * Returns a random [Ubit] of width [N].
 */
fun <N : `*`> randomUbit(): Ubit<N> {
    throw VerikException()
}
