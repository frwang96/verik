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
class _ubit internal constructor(
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
    fun unpack(x: _bool) = false

    /**
     * (UNIMPLEMENTED) ???
     */
    fun unpack(x: _int) = 0

    /**
     * (UNIMPLEMENTED) ???
     */
    fun <T: _data> unpack(x: T) = x

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

    @Suppress("FunctionName")
    operator fun unaryPlus(): _ubit {
        throw VerikDslException()
    }

    @Suppress("FunctionName")
    operator fun unaryMinus(): _ubit {
        throw VerikDslException()
    }

    @Suppress("FunctionName")
    operator fun compareTo(x: _ubit): _int {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun inc(): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun dec(): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun not(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun plus(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun plus(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun minus(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun minus(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun times(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun times(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun rem(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun rem(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun div(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun div(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun is_unknown(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun _ubit.set(x: _ubit) {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) ???
 */
fun ubit(SIZE: _int, value: _int): _ubit {
    return _ubit(SIZE, value.toString())
}

/**
 * (UNIMPLEMENTED) ???
 */
fun ubit(value: _int): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
fun ubit(value: _string): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
fun ubit(value: _sbit): _ubit {
    throw VerikDslException()
}
