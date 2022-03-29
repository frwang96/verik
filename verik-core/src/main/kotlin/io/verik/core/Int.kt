/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * Convert this value to a [Ubit] of width [N].
 */
fun <N : `*`> Int.toUbit(): Ubit<N> {
    throw VerikException()
}

/**
 * Convert this value to an [Sbit] of width [N].
 */
fun <N : `*`> Int.toSbit(): Sbit<N> {
    throw VerikException()
}
