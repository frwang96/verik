/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * A cluster of type [T] and size [N]. Clusters can be used to declare multiple [components][Component]. Construct a
 * cluster with the [cluster] function. Clusters are statically elaborated by the compiler.
 *
 *  ```
 *  class M : Module()
 *
 *  class Top : Module() {
 *
 *      @Make
 *      val m = cluster(4) { M() }
 *  }
 *  ```
 */
class Cluster<N : `*`, T> private constructor() : Component(), Iterable<T> {

    override fun iterator(): Iterator<T> {
        throw VerikException()
    }

    /**
     * Returns the component at the specified [index]. [index] must either be a compile time constant or a loop index
     * with bounds that are compile time constants.
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
