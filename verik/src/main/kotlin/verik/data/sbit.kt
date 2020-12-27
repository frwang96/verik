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

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "unused")

package verik.data

import verik.base.*

/**
 * (UNIMPLEMENTED) ???
 */
class _sbit internal constructor(
    val SIZE: _int,
    internal val value: _string
): _data {

    /**
     * (UNIMPLEMENTED) ???
     */
    constructor(SIZE: _int): this(SIZE, "")

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun get(n: _int): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun get(n: _ubit): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun get(n: _int, m: _int): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun set(n: _int, value: _bool) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun set(n: _ubit, value: _bool) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun set(n: _int, m: _int, value: _ubit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun is_unknown(): _bool {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) ???
 */
fun sbit(SIZE: _int, value: _int): _sbit {
    return _sbit(SIZE, value.toString())
}

/**
 * (UNIMPLEMENTED) ???
 */
fun sbit(value: _int): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
fun sbit(value: _string): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
fun sbit(value: _ubit): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
infix fun _sbit.set(x: _sbit) {
    throw VerikDslException()
}
