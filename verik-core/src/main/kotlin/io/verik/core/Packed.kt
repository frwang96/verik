/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A packed array of size [N] of elements of type [E].
 */
class Packed<N : `*`, E> private constructor() : Iterable<E> {

    override fun iterator(): Iterator<E> {
        throw VerikException()
    }

    /**
     * The size of the array.
     */
    var size: Int by VerikExceptionDelegate()

    /**
     * Returns the array element at the specified [index].
     */
    operator fun get(index: Int): E {
        throw VerikException()
    }

    /**
     * Returns the array element at the specified [index].
     */
    operator fun get(index: Ubit<`*`>): E {
        throw VerikException()
    }

    /**
     * Sets the array element at the specified [index] to the specified [element].
     */
    operator fun set(index: Int, element: E) {
        throw VerikException()
    }

    /**
     * Sets the array element at the specified [index] to the specified [element].
     */
    operator fun set(index: Ubit<`*`>, element: E) {
        throw VerikException()
    }
}
