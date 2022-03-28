/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * A queue of elements of type [E]. It corresponds to a SystemVerilog queue.
 */
class Queue<E> private constructor() : Iterable<E> {

    /**
     * The size of the queue.
     */
    var size: Int by VerikExceptionDelegate()

    override fun iterator(): Iterator<E> {
        throw VerikException()
    }

    /**
     * Add element [e] to the end of this queue.
     */
    fun add(e: E) {
        throw VerikException()
    }

    /**
     * Returns the element at the specified [index].
     */
    operator fun get(index: Int): E {
        throw VerikException()
    }
}
