/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * An unpacked array of size [X] of elements of type [E].
 */
class Unpacked<X : `*`, E> private constructor() : Iterable<E> {

    /**
     * The size of the array.
     */
    var size: Int by VerikExceptionDelegate()

    override fun iterator(): Iterator<E> {
        throw VerikException()
    }

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
