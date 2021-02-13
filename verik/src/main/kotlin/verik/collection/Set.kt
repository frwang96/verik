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
 * (UNIMPLEMENTED) A set of [TYPE].
 */
class Set<Type>(val TYPE: Type): Collection(), Iterable<Type> {

    /**
     * (UNIMPLEMENTED) Add [x] to the set.
     */
    fun add(x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if the set contains [x].
     */
    fun contains(x: Type): Boolean {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Remove [x] from the set.
     */
    fun remove(x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the elements of the set.
     */
    fun for_each(block: (Type) -> Unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Clears the contents of the set.
     */
    fun clear() {
        throw VerikDslException()
    }

    override fun iterator(): Iterator<Type> {
        throw VerikDslException()
    }
}
