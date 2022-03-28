/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * An associative array of elements with keys of type [K] and values of type [V]. It corresponds to a SystemVerilog
 * associative array.
 */
class AssociativeArray<K, V> private constructor() {

    /**
     * Set the [value] corresponding to [key].
     */
    operator fun set(key: K, value: V) {
        throw VerikException()
    }

    /**
     * Get the value corresponding to [key].
     */
    operator fun get(key: K): V {
        throw VerikException()
    }
}
