/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.kotlin

/**
 * Detects a common minimal indent of all the input lines, removes it from every line and also removes the first and
 * the last lines if they are blank.
 */
fun String.trimIndent(): String {
    throw VerikException()
}
