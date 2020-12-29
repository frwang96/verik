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
): _data() {

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

    @Suppress("FunctionName")
    operator fun unaryPlus(): _sbit {
        throw VerikDslException()
    }

    @Suppress("FunctionName")
    operator fun unaryMinus(): _sbit {
        throw VerikDslException()
    }

    @Suppress("FunctionName")
    operator fun compareTo(x: _sbit): _int {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun inc(): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    operator fun dec(): _sbit {
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
    operator fun plus(x: _sbit): _sbit {
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
    operator fun minus(x: _sbit): _sbit {
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
    operator fun times(x: _sbit): _sbit {
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
    operator fun rem(x: _sbit): _sbit {
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
    operator fun div(x: _sbit): _sbit {
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
    infix fun add(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun add(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun sub(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun sub(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun mul(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun mul(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun sl(x: _int): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun sl(x: _ubit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun sr(x: _int): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun sr(x: _ubit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun and(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun and(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun or(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun or(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun xor(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun xor(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun nand(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun nand(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun nor(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun nor(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun xnor(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun xnor(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun inv(): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun red_and(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun red_nand(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun red_or(): _bool {
        throw VerikDslException()
    }
    /**
     * (UNIMPLEMENTED) ???
     */
    fun red_nor(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun red_xor(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun red_xnor(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun ext(len: _int): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun tru(len: _int): _sbit {
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
    fun is_x(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun is_z(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun to_x(): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    fun to_z(): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) ???
     */
    infix fun set(x: _sbit) {
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
