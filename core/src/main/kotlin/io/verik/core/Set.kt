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

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A set of elements.
 * @param T the type of the set
 */
class Set<T>: Collection(), Iterable<T> {

    override fun iterator(): Iterator<T> {
        throw VerikException()
    }

    /**
     * Add an element to the set.
     * @param x the element to add
     */
    fun add(x: T) {
        throw VerikException()
    }

    /**
     * Returns true if the set contains a particular element.
     * @param x the element to search for
     */
    fun contains(x: T): Boolean {
        throw VerikException()
    }

    /**
     * Remove a particular element from the set.
     * @param x the element to remove
     */
    fun remove(x: T) {
        throw VerikException()
    }

    /**
     * Clear the contents of the set.
     */
    fun clear() {
        throw VerikException()
    }
}
