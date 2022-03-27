/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * (UNIMPLEMENTED) Returns true if the boolean is unknown.
 */
fun Boolean.hasx(): Boolean {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Returns true if the boolean is floating.
 */
fun Boolean.hasz(): Boolean {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Returns true if the boolean is unknown or floating.
 */
fun Boolean.hasxz(): Boolean {
    throw VerikException()
}

/**
 * Converts this value to a [Ubit] of width [N]. The value is extended with zero extension.
 */
fun <N : `*`> Boolean.toUbit(): Ubit<N> {
    throw VerikException()
}

/**
 * Converts this value to an [Sbit] of width [N]. The value is extended with sign extension.
 */
fun <N : `*`> Boolean.toSbit(): Sbit<N> {
    throw VerikException()
}
