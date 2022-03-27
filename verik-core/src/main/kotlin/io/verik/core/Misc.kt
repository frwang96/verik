/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Returns the concatenation of the arguments.
 */
fun cat(value: Any, vararg additional_values: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns [value] replicated [N] times.
 */
fun <N : `*`> rep(value: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(value: Int, vararg additional_values: Int): Int {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(value: Ubit<`*`>, vararg additional_values: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(value: Sbit<`*`>, vararg additional_values: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(value: Int, vararg additional_values: Int): Int {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(value: Ubit<`*`>, vararg additional_values: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(value: Sbit<`*`>, vararg additional_values: Sbit<`*`>): Sbit<`*`> {
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
