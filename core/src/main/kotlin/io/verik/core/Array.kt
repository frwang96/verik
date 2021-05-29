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
 * An array of fixed size.
 * @param N the size of the array
 * @param T the type of the array
 */
class Array<N: Cardinal, T>: Collection(), Iterable<T> {

    override fun iterator(): Iterator<T> {
        throw VerikException()
    }

    /**
     * Get an element of the array.
     * @param n the index of the element
     * @return the element at index [n]
     */
    operator fun get(n: Int): T {
        throw VerikException()
    }

    /**
     * Get an element of the array.
     * @param n the index of the element
     * @return the element at index [n]
     */
    operator fun get(n: Ubit<Cardinal>): T {
        throw VerikException()
    }

    /**
     * Set an element of the array.
     * @param n the index of the element
     * @param x the value to set
     */
    operator fun set(n: Int, x: T) {
        throw VerikException()
    }

    /**
     * Set an element of the array.
     * @param n the index of the element
     * @param x the value to set
     */
    operator fun set(n: Ubit<Cardinal>, x: T) {
        throw VerikException()
    }

    /**
     * Clear the contents of the array.
     */
    fun clear() {
        throw VerikException()
    }
}
