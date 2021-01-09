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
 * (UNIMPLEMENTED) A generic indexed collection.
 */
abstract class _indexed<TYPE>(val _TYPE: TYPE): _collection(), Iterable<TYPE> {

    /**
     * (UNIMPLEMENTED) Get element [n] of the collection.
     */
    operator fun get(n: _int): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Get element [n] of the collection.
     */
    operator fun get(n: _ubit): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set element [n] of the collection.
     */
    operator fun set(n: _int, x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set element [n] of the collection.
     */
    operator fun set(n: _ubit, x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the elements of the collection.
     */
    infix fun for_each(block: (TYPE) -> _unit) {
        throw VerikDslException()
    }

    override fun iterator(): Iterator<TYPE> {
        throw VerikDslException()
    }
}
