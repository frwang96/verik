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
 * An immutable list of elements.
 * @property TYPE the type of the list.
 */
class List<Type>(val TYPE: Type): Collection(), Iterable<Type> {

    override fun iterator(): Iterator<Type> {
        throw VerikDslException()
    }

    /**
     * Get an element of the list.
     * @param n the index of the element
     * @return the element at index [n]
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Int): Type {
        throw VerikDslException()
    }

    /**
     * Get an element of the list.
     * @param n the index of the element
     * @return the element at index [n]
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Ubit): Type {
        throw VerikDslException()
    }

    /**
     * Iterate over the elements of the list.
     * @param block the block to execute for each iteration
     */
    @Deprecated("UNIMPLEMENTED")
    fun for_each(block: (Type) -> Unit) {
        throw VerikDslException()
    }
}
