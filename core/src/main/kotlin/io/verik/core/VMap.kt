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
class VMap<K, V> {

    /**
     * Checks if the map contains the specified [key].
     */
    operator fun contains(key: K): Boolean {
        throw VerikException()
    }

    /**
     * Returns the value corresponding to the specified [key].
     */
    operator fun get(key: K): V {
        throw VerikException()
    }

    /**
     * Sets the [value] corresponding to the specified [key].
     */
    operator fun set(key: K, value: V) {
        throw VerikException()
    }

    /**
     * Removes the value with the specified [key].
     */
    fun remove(key: K) {
        throw VerikException()
    }

    /**
     * Clear the contents of the map.
     */
    fun clear() {
        throw VerikException()
    }
}
