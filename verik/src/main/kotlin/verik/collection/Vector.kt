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
import verik.data.*

/**
 * A vector of elements that can grow and shrink dynamically.
 * @property TYPE the type of the vector
 */
class Vector<Type>(val TYPE: Type): Collection(), Iterable<Type> {

    override fun iterator(): Iterator<Type> {
        throw VerikException()
    }

    /**
     * Get an element of the vector.
     * @param n the index of the element
     * @return the element at index [n]
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Int): Type {
        throw VerikException()
    }

    /**
     * Get an element of the vector.
     * @param n the index of the element
     * @return the element at index [n]
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Ubit): Type {
        throw VerikException()
    }

    /**
     * Set an element of the vector.
     * @param n the index of the element
     * @param x the value to set
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun set(n: Int, x: Type) {
        throw VerikException()
    }

    /**
     * Set an element of the vector.
     * @param n the index of the element
     * @param x the value to set
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun set(n: Ubit, x: Type) {
        throw VerikException()
    }

    /**
     * Iterate over the elements of the vector.
     * @param block the block fo execute for each iteration
     */
    @Deprecated("UNIMPLEMENTED")
    fun for_each(block: (Type) -> Unit) {
        throw VerikException()
    }

    /**
     * Add an element to the vector.
     * @param x the element to add
     */
    @Deprecated("UNIMPLEMENTED")
    fun add(x: Type) {
        throw VerikException()
    }

    /**
     * Add an element to the vector at a particular position.
     * @param n the position to add at
     * @param x the element to add
     */
    @Deprecated("UNIMPLEMENTED")
    fun insert(n: Int, x: Type) {
        throw VerikException()
    }

    /**
     * Remove the element at a particular position.
     * @param n the position to remove at
     */
    @Deprecated("UNIMPLEMENTED")
    fun remove(n: Int) {
        throw VerikException()
    }

    /**
     * Clear the contents of the vector.
     */
    @Deprecated("UNIMPLEMENTED")
    fun clear() {
        throw VerikException()
    }
}
