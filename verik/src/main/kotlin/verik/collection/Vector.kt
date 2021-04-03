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
 * (UNIMPLEMENTED) A vector of elements that can grow and shrink dynamically.
 * @property TYPE the type of the vector
 */
class Vector<Type>(val TYPE: Type): Collection(), Iterable<Type> {

    /**
     * (UNIMPLEMENTED) Get an element of the vector.
     * @param n the index of the element
     * @return the element at index [n]
     */
    operator fun get(n: Int): Type {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Get an element of the vector.
     * @param n the index of the element
     * @return the element at index [n]
     */
    operator fun get(n: Ubit): Type {
        throw VerikDslException()
    }

    /**
     * Set an element of the vector.
     * @param n the index of the element
     * @param x the value to set
     */
    operator fun set(n: Int, x: Type) {
        throw VerikDslException()
    }

    /**
     * Set an element of the vector.
     * @param n the index of the element
     * @param x the value to set
     */
    operator fun set(n: Ubit, x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the elements of the vector.
     * @param block the block fo execute for each iteration
     */
    fun for_each(block: (Type) -> Unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Add an element to the vector.
     * @param x the element to add
     */
    fun add(x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Add an element to the vector at a particular position.
     * @param n the position to add at
     * @param x the element to add
     */
    fun insert(n: Int, x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Remove the element at a particular position.
     * @param n the position to remove at
     */
    fun remove(n: Int) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Clear the contents of the vector.
     */
    fun clear() {
        throw VerikDslException()
    }

    override fun iterator(): Iterator<Type> {
        throw VerikDslException()
    }
}
