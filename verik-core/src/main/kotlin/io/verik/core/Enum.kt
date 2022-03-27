/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * (UNIMPLEMENTED) Returns true if the enum is unknown.
 */
fun <E : Enum<E>> Enum<E>.hasx(): Boolean {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Returns true if the enum is floating.
 */
fun <E : Enum<E>> Enum<E>.hasz(): Boolean {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Returns true if the enum is unknown or floating.
 */
fun <E : Enum<E>> Enum<E>.hasxz(): Boolean {
    throw VerikException()
}
