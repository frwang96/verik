/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Returns the concatenation of the [values]. [values] should not be empty.
 */
fun cat(vararg values: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns [value] replicated [N] times.
 */
fun <N : `*`> rep(value: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the [values]. [values] should have at least two arguments.
 */
fun max(vararg values: Int): Int {
    throw VerikException()
}

/**
 * Returns the maximum of the [values]. [values] should have at least two arguments
 */
fun max(vararg values: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the [values]. [values] should have at least two arguments
 */
fun max(vararg values: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the [values]. [values] should have at least two arguments
 */
fun min(vararg values: Int): Int {
    throw VerikException()
}

/**
 * Returns the minimum of the [values]. [values] should have at least two arguments
 */
fun min(vararg values: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the [values]. [values] should have at least two arguments
 */
fun min(vararg values: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the ceiling logarithm base two of [value].
 */
fun log(value: Int): Int {
    throw VerikException()
}

/**
 * Returns the exponential base two of [value].
 */
fun exp(value: Int): Int {
    throw VerikException()
}
