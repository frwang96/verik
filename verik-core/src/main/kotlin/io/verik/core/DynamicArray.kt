/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * A dynamic array of elements of type [E]. It corresponds to a SystemVerilog dynamic array.
 */
class DynamicArray<E> private constructor() {

    /**
     * Returns the element at the specified [index].
     */
    operator fun get(index: Int): E {
        throw VerikException()
    }

    /**
     * Sets the element at the specified [index] to [value].
     */
    operator fun set(index: Int, value: E) {
        throw VerikException()
    }

    /**
     * Returns the number of elements in this array.
     */
    fun size(): Int {
        throw VerikException()
    }
}
