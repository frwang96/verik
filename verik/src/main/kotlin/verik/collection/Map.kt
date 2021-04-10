/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER", "unused", "MemberVisibilityCanBePrivate")

package verik.collection

import verik.base.*

/**
 * A map from keys to values.
 * @property KEY the type of the key
 * @property VALUE the type of the value
 */
class Map<Key, Value>(val KEY: Key, val VALUE: Value): Collection() {

    /**
     * Get a value from the map.
     * @param key the key of the element
     * @return the element corresponding to [key]
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(key: Key): Value {
        throw VerikDslException()
    }

    /**
     * Set a value of the map.
     * @param key the key of the element
     * @param value the value to set
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun set(key: Key, value: Value) {
        throw VerikDslException()
    }

    /**
     * Returns true if the map contains an element with a particular key.
     * @param key the key of the element
     */
    @Deprecated("UNIMPLEMENTED")
    fun contains(key: Key): Boolean {
        throw VerikDslException()
    }

    /**
     * Remove an element from the map with a particular key.
     * @param key the key of the element
     */
    @Deprecated("UNIMPLEMENTED")
    fun remove(key: Key) {
        throw VerikDslException()
    }

    /**
     * Iterate over the keys of the map.
     * @param block the block to execute for each key
     */
    @Deprecated("UNIMPLEMENTED")
    fun for_keys(block: (Key) -> Unit) {
        throw VerikDslException()
    }

    /**
     * Iterate over the values of the map.
     * @param block the block to execute for each value
     */
    @Deprecated("UNIMPLEMENTED")
    fun for_values(block: (Value) -> Unit) {
        throw VerikDslException()
    }

    /**
     * Iterate over the keys and values of the map.
     * @param block the block to execute for each key and value
     */
    @Deprecated("UNIMPLEMENTED")
    fun for_each(block: (Key, Value) -> Unit) {
        throw VerikDslException()
    }

    /**
     * Clear the contents of the map.
     */
    @Deprecated("UNIMPLEMENTED")
    fun clear() {
        throw VerikDslException()
    }
}
