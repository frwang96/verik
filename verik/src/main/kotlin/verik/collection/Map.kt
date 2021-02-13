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
 * (UNIMPLEMENTED) A map with [KEY] and [VALUE].
 */
class Map<Key, Value>(val KEY: Key, val VALUE: Value): Collection() {

    /**
     * (UNIMPLEMENTED) Get the value corresponding to [key].
     */
    operator fun get(key: Key): Value {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set the value corresponding to [key].
     */
    operator fun set(key: Key, value: Value) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if the map contains [key].
     */
    fun contains(key: Key): Boolean {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Remove [key].
     */
    fun remove(key: Key) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the keys of the map.
     */
    fun for_keys(block: (Key) -> Unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the values of the map.
     */
    fun for_values(block: (Value) -> Unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the keys and values of the map.
     */
    fun for_each(block: (Key, Value) -> Unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Clears the contents of the map.
     */
    fun clear() {
        throw VerikDslException()
    }
}
