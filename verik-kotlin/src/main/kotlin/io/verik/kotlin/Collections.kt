/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.kotlin

/**
 * Performs the given [action] on each element.
 */
fun <T> Iterable<T>.forEach(action: (T) -> Unit) {
    throw VerikException()
}
