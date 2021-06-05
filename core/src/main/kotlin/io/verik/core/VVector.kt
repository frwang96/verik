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
 * @param E the type of the elements of the vector
 */
class VVector<E>: VCollection<E>() {

    /**
     * Returns the vector element at the specified [index].
     */
    operator fun get(index: Int): E {
        throw VerikException()
    }

    /**
     * Returns the vector element at the specified [index].
     */
    operator fun get(index: Ubit<Cardinal>): E {
        throw VerikException()
    }

    /**
     * Sets the vector element at the specified [index] to the specified [element].
     */
    operator fun set(index: Int, element: E) {
        throw VerikException()
    }

    /**
     * Sets the vector element at the specified [index] to the specified [element].
     */
    operator fun set(index: Ubit<Cardinal>, element: E) {
        throw VerikException()
    }

    /**
     * Adds the specified [element].
     */
    fun add(element: E) {
        throw VerikException()
    }

    /**
     * Inserts the specified [element] at the specified [index].
     */
    fun insert(index: Int, element: E) {
        throw VerikException()
    }

    /**
     * Removes the element at the specified [index].
     */
    fun remove(index: Int) {
        throw VerikException()
    }

    /**
     * Clears the contents of the vector.
     */
    fun clear() {
        throw VerikException()
    }
}
