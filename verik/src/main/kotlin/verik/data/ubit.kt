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
 * Represents an sequence of bits of [WIDTH] that is unsigned.
 */
class _ubit internal constructor(
    val WIDTH: _int,
    internal val value: _string
): _logic() {

    /**
     * Returns the type of [_ubit] with [WIDTH].
     */
    constructor(WIDTH: _int): this(WIDTH, "")

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: _ubit) {
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

    fun to_x(): _ubit {
        throw VerikDslException()
    }

    fun to_z(): _ubit {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

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
     * (UNIMPLEMENTED) Unpack with type [x].
     */
    fun <T: _data> unpack(x: T): T {
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
     * Get range from bits [n] to [m].
     */
    operator fun get(n: _int, m: _int): _ubit {
        throw VerikDslException()
    }

    /**
     * Set bit [n].
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
     * Set range from bits [n] to [m].
     */
    operator fun set(n: _int, m: _int, value: _ubit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Increment by one.
     */
    operator fun inc(): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Decrement by one.
     */
    operator fun dec(): _ubit {
        throw VerikDslException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): _bool {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_ubit(n) + _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun plus(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_ubit(n) + _sbit(m) = _ubit(max(n, m))`.
     */
    operator fun plus(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_ubit(n) - _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_ubit(n) - _sbit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_ubit(n) * _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_ubit(n) * _sbit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width  `_ubit(n) + _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width `_ubit(n) + _sbit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_ubit(n) - _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_ubit(n) - _sbit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width `_ubit(n) * _ubit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width  `_ubit(n) * _sbit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    infix fun sl(x: _int): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Logical left shift by [x] bits.
     */
    infix fun sl(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Logical right shift by [x] bits.
     */
    infix fun sr(x: _int): _ubit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Logical right shift by [x] bits.
     */
    infix fun sr(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: _ubit): _ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: _sbit): _ubit {
        throw VerikDslException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): _ubit {
        throw VerikDslException()
    }

    /**
     * Reduce bits with and.
     */
    fun red_and(): _bool {
        throw VerikDslException()
    }

    /**
     * Reduce bits with or.
     */
    fun red_or(): _bool {
        throw VerikDslException()
    }

    /**
     * Reduce bits with xor.
     */
    fun red_xor(): _bool {
        throw VerikDslException()
    }

    /**
     * Extend to [width] with zero extension.
     */
    fun ext(width: _int): _ubit {
        throw VerikDslException()
    }

    /**
     * Truncate to [width].
     */
    fun tru(width: _int): _ubit {
        throw VerikDslException()
    }
}

/**
 * Constructs a [_ubit] of [WIDTH] and [value].
 */
fun ubit(WIDTH: _int, value: _int): _ubit {
    return _ubit(WIDTH, value.toString())
}

/**
 * Constructs a [_ubit] of [value]. The width is automatically inferred from the context.
 */
fun ubit(value: _int): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a [_ubit] of [value].
 */
fun ubit(value: _string): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a [_ubit] of [value].
 */
fun ubit(value: _sbit): _ubit {
    throw VerikDslException()
}
