/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * A cluster of type [T] and size [N].
 */
class Cluster<N : `*`, T> private constructor() : Component(), Iterable<T> {

    override fun iterator(): Iterator<T> {
        throw VerikException()
    }

    /**
     * Returns the component at the specified [index].
     */
    operator fun get(index: Int): T {
        throw VerikException()
    }

    /**
     * Returns a cluster containing the results of applying the given [transform] to each element in the original
     * cluster.
     */
    fun <R> map(transform: (T) -> R): Cluster<N, R> {
        throw VerikException()
    }
}
