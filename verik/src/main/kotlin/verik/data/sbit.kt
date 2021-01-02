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

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "unused")

package verik.data

import verik.base.*

/**
 * (UNIMPLEMENTED) Represents a sequence of bits of [WIDTH] that is signed.
 */
class _sbit internal constructor(
    val WIDTH: _int,
    internal val value: _string
): _logic() {

    /**
     * Returns the type of [_sbit] with [WIDTH].
     */
    constructor(WIDTH: _int): this(WIDTH, "")

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: _sbit) {
        throw VerikDslException()
    }

    fun pack(): _ubit {
        throw VerikDslException()
    }

    fun is_unknown(): _bool {
        throw VerikDslException()
    }

    fun is_x(): _bool {
        throw VerikDslException()
    }

    fun is_z(): _bool {
        throw VerikDslException()
    }

    fun to_x(): _sbit {
        throw VerikDslException()
    }

    fun to_z(): _sbit {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

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
     * Get bit [n].
     */
    operator fun get(n: _int): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Get bit [n].
     */
    operator fun get(n: _ubit): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Get range from bits [n] to [m].
     */
    operator fun get(n: _int, m: _int): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set bit [n].
     */
    operator fun set(n: _int, value: _bool) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set bit [n].
     */
    operator fun set(n: _ubit, value: _bool) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set range from bits [n] to [m].
     */
    operator fun set(n: _int, m: _int, value: _ubit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Increment by one.
     */
    operator fun inc(): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Decrement by one.
     */
    operator fun dec(): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if zero.
     */
    operator fun not(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Add [x] to this value with truncated bit width.
     */
    operator fun plus(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated bit width.
     */
    operator fun plus(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Subtract [x] from this value with truncated bit width.
     */
    operator fun minus(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Subtract [x] from this value with truncated bit width.
     */
    operator fun minus(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Multiply [x] to this value with truncated bit width.
     */
    operator fun times(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Multiply [x] to this value with truncated bit width.
     */
    operator fun times(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the remainder when divided by [x].
     */
    operator fun rem(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the remainder when divided by [x].
     */
    operator fun rem(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the quotient when divided by [x].
     */
    operator fun div(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the quotient when divided by [x].
     */
    operator fun div(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Add [x] to this value with full bit width.
     */
    infix fun add(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Add [x] to this value with full bit width.
     */
    infix fun add(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Subtract [x] from this value with full bit width.
     */
    infix fun sub(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Subtract [x] from this value with full bit width.
     */
    infix fun sub(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Multiply [x] to this value with full bit width.
     */
    infix fun mul(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Multiply [x] to this value with full bit width.
     */
    infix fun mul(x: _sbit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Shift left by [x] bits.
     */
    infix fun sl(x: _int): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Shift left by [x] bits.
     */
    infix fun sl(x: _ubit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Shift right by [x] bits.
     */
    infix fun sr(x: _int): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Shift right by [x] bits.
     */
    infix fun sr(x: _ubit): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the bitwise and.
     */
    infix fun and(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the bitwise and.
     */
    infix fun and(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the bitwise or.
     */
    infix fun or(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the bitwise or.
     */
    infix fun or(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the bitwise xor.
     */
    infix fun xor(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Takes the bitwise xor.
     */
    infix fun xor(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Inverts the bits.
     */
    fun inv(): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Reduce bits with and.
     */
    fun red_and(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Reduce bits with or.
     */
    fun red_or(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Reduce bits with xor.
     */
    fun red_xor(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Extend to [width].
     */
    fun ext(width: _int): _sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Truncate to [width].
     */
    fun tru(width: _int): _sbit {
        throw VerikDslException()
    }
}

/**
 * Constructs a [_sbit] of [WIDTH] and [value].
 */
fun sbit(WIDTH: _int, value: _int): _sbit {
    return _sbit(WIDTH, value.toString())
}

/**
 * Constructs a [_sbit] of [value]. The width is automatically inferred from the context.
 */
fun sbit(value: _int): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a [_sbit] of [value].
 */
fun sbit(value: _string): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a [_sbit] of [value].
 */
fun sbit(value: _ubit): _sbit {
    throw VerikDslException()
}
