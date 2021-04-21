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
 * A set of elements.
 * @property TYPE the type of the set
 */
class Set<Type>(val TYPE: Type): Collection(), Iterable<Type> {

    override fun iterator(): Iterator<Type> {
        throw VerikException()
    }

    /**
     * Add an element to the set.
     * @param x the element to add
     */
    @Deprecated("UNIMPLEMENTED")
    fun add(x: Type) {
        throw VerikException()
    }

    /**
     * Returns true if the set contains a particular element.
     * @param x the element to search for
     */
    @Deprecated("UNIMPLEMENTED")
    fun contains(x: Type): Boolean {
        throw VerikException()
    }

    /**
     * Remove a particular element from the set.
     * @param x the element to remove
     */
    @Deprecated("UNIMPLEMENTED")
    fun remove(x: Type) {
        throw VerikException()
    }

    /**
     * Iterate over the elements of the set.
     * @param block the block to execute for each element
     */
    @Deprecated("UNIMPLEMENTED")
    fun for_each(block: (Type) -> Unit) {
        throw VerikException()
    }

    /**
     * Clear the contents of the set.
     */
    @Deprecated("UNIMPLEMENTED")
    fun clear() {
        throw VerikException()
    }
}
