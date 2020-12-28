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

/**
 * (UNIMPLEMENTED) ???
 */
class _map<KEY, VALUE>(val _KEY: KEY, val _VALUE: VALUE): _collection {

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun get(key: KEY): VALUE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun set(key: KEY, value: VALUE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun contains(key: KEY): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun remove(key: KEY) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun set(x: _map<KEY, VALUE>) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun for_keys(block: (KEY) -> _unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun for_values(block: (VALUE) -> _unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun for_each(block: (KEY, VALUE) -> _unit) {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) ???
 */
fun <KEY, VALUE> map(_KEY: KEY, _VALUE: VALUE): _map<KEY, VALUE> {
    throw VerikDslException()
}
