/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * A map from keys to values.
 * @param K the type of the key
 * @param V the type of the value
 */
class Map<K, V>: Collection() {

    /**
     * Get a value from the map.
     * @param key the key of the element
     * @return the element corresponding to [key]
     */
    operator fun get(key: K): V {
        throw VerikException()
    }

    /**
     * Set a value of the map.
     * @param key the key of the element
     * @param value the value to set
     */
    operator fun set(key: K, value: V) {
        throw VerikException()
    }

    /**
     * Returns true if the map contains an element with a particular key.
     * @param key the key of the element
     */
    fun contains(key: K): Boolean {
        throw VerikException()
    }

    /**
     * Remove an element from the map with a particular key.
     * @param key the key of the element
     */
    fun remove(key: K) {
        throw VerikException()
    }

    /**
     * Iterate over the keys of the map.
     * @param block the block to execute for each key
     */
    fun forKeys(block: (K) -> Unit) {
        throw VerikException()
    }

    /**
     * Iterate over the values of the map.
     * @param block the block to execute for each value
     */
    fun forValues(block: (V) -> Unit) {
        throw VerikException()
    }

    /**
     * Iterate over the keys and values of the map.
     * @param block the block to execute for each key and value
     */
    fun forEach(block: (K, V) -> Unit) {
        throw VerikException()
    }

    /**
     * Clear the contents of the map.
     */
    fun clear() {
        throw VerikException()
    }
}
