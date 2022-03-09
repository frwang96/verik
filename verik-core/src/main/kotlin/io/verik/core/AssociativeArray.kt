/*
 * Copyright (c) 2022 Francis Wang
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
 * An associative array of elements with key [K] and value [V]. It corresponds to a SystemVerilog associative array.
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
