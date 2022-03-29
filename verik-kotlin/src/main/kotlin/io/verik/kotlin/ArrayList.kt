/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.kotlin

/**
 * Resizable array of elements of type [E].
 */
class ArrayList<E> : Iterable<E> {

    /**
     * Returns the number of elements in this list.
     */
    val size: Int by lazy<Int> {
        throw VerikException()
    }

    /**
     * Appends element [e] to the end of this list.
     */
    fun add(e: E) {
        throw VerikException()
    }

    /**
     * Returns the element at the specified [index] in this list.
     */
    fun get(index: Int): E {
        throw VerikException()
    }

    /**
     * Replaces the element at the specified [index] in this list.
     */
    fun set(index: Int, element: E) {
        throw VerikException()
    }
}
