/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.kotlin

/**
 * Executes the given function [action] specified number of [times]. A zero-based index of current iteration is passed
 * as a parameter to [action].
 */
fun repeat(times: Int, action: (Int) -> Unit) {
    throw VerikException()
}

/**
 * Asserts that [value] is true. Raises an error otherwise.
 */
fun assert(value: Boolean) {
    throw VerikException()
}

/**
 * Asserts that [value] is true. Executes [action] otherwise.
 */
fun assert(value: Boolean, action: () -> Any) {
    throw VerikException()
}
