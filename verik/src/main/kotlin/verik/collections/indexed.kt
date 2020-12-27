/*
 * Copyright 2020 Francis Wang
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

package verik.collections

import verik.base.*
import verik.data.*

/**
 * (UNIMPLEMENTED) ???
 */
interface _indexed<TYPE>: _collection, Iterable<TYPE> {

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun get(n: _int): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun get(n: _ubit): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun set(n: _int, x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun set(n: _ubit, x: TYPE) {
        throw VerikDslException()
    }

    override fun iterator(): Iterator<TYPE> {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) ???
 */
infix fun <TYPE> _indexed<TYPE>.for_each(block: (TYPE) -> _unit) {
    throw VerikDslException()
}
