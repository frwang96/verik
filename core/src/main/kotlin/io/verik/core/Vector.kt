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
 * A vector of elements that can grow and shrink dynamically.
 * @param T the type of the vector
 */
class Vector<T>: Collection(), Iterable<T> {

    override fun iterator(): Iterator<T> {
        throw VerikException()
    }

    /**
     * Get an element of the vector.
     * @param n the index of the element
     * @return the element at index [n]
     */
    operator fun get(n: Int): T {
        throw VerikException()
    }

    /**
     * Get an element of the vector.
     * @param n the index of the element
     * @return the element at index [n]
     */
    operator fun get(n: Ubit<Cardinal>): T {
        throw VerikException()
    }

    /**
     * Set an element of the vector.
     * @param n the index of the element
     * @param x the value to set
     */
    operator fun set(n: Int, x: T) {
        throw VerikException()
    }

    /**
     * Set an element of the vector.
     * @param n the index of the element
     * @param x the value to set
     */
    operator fun set(n: Ubit<Cardinal>, x: T) {
        throw VerikException()
    }

    /**
     * Add an element to the vector.
     * @param x the element to add
     */
    fun add(x: T) {
        throw VerikException()
    }

    /**
     * Add an element to the vector at a particular position.
     * @param n the position to add at
     * @param x the element to add
     */
    fun insert(n: Int, x: T) {
        throw VerikException()
    }

    /**
     * Remove the element at a particular position.
     * @param n the position to remove at
     */
    fun remove(n: Int) {
        throw VerikException()
    }

    /**
     * Clear the contents of the vector.
     */
    fun clear() {
        throw VerikException()
    }
}
