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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.collection

import verik.base.*
import verik.data.*

/**
 * (UNIMPLEMENTED) A set of [_TYPE].
 */
class _set<TYPE>(val _TYPE: TYPE): _collection(), Iterable<TYPE> {

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: _set<TYPE>) {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    /**
     * (UNIMPLEMENTED) Add [x] to the set.
     */
    fun add(x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if the set contains [x].
     */
    fun contains(x: TYPE): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Remove [x] from the set.
     */
    fun remove(x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the elements of the set.
     */
    infix fun for_each(block: (TYPE) -> _unit) {
        throw VerikDslException()
    }

    override fun iterator(): Iterator<TYPE> {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) Construct a set of [_TYPE].
 */
fun <TYPE> set(_TYPE: TYPE): _set<TYPE> {
    throw VerikDslException()
}