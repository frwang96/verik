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
class Sbit internal constructor(
    val WIDTH: Int,
    internal val value: String
): Logic() {

    /**
     * Returns the type of [Sbit] with [WIDTH].
     */
    constructor(WIDTH: Int): this(WIDTH, "")

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: Sbit) {
        throw VerikDslException()
    }

    fun pack(): Ubit {
        throw VerikDslException()
    }

    fun is_unknown(): Boolean {
        throw VerikDslException()
    }

    fun is_x(): Boolean {
        throw VerikDslException()
    }

    fun is_z(): Boolean {
        throw VerikDslException()
    }

    fun to_x(): Sbit {
        throw VerikDslException()
    }

    fun to_z(): Sbit {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    @Suppress("FunctionName")
    operator fun unaryPlus(): Sbit {
        throw VerikDslException()
    }

    @Suppress("FunctionName")
    operator fun unaryMinus(): Sbit {
        throw VerikDslException()
    }

    @Suppress("FunctionName")
    operator fun compareTo(x: Sbit): Int {
        throw VerikDslException()
    }

    /**
     * Get bit [n].
     */
    operator fun get(n: Int): Boolean {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Get bit [n].
     */
    operator fun get(n: Ubit): Boolean {
        throw VerikDslException()
    }

    /**
     * Get range from bits [n] to [m].
     */
    operator fun get(n: Int, m: Int): Ubit {
        throw VerikDslException()
    }

    /**
     * Set bit [n].
     */
    operator fun set(n: Int, value: Boolean) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set bit [n].
     */
    operator fun set(n: Ubit, value: Boolean) {
        throw VerikDslException()
    }

    /**
     * Set range from bits [n] to [m].
     */
    operator fun set(n: Int, m: Int, value: Ubit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Increment by one.
     */
    operator fun inc(): Sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Decrement by one.
     */
    operator fun dec(): Sbit {
        throw VerikDslException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_sbit(n) + _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun plus(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_sbit(n) + _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun plus(x: Sbit): Sbit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_sbit(n) - _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_sbit(n) - _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun minus(x: Sbit): Sbit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_sbit(n) * _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_sbit(n) * _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun times(x: Sbit): Sbit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width `_sbit(n) + _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width `_sbit(n) + _sbit(m) = _sbit(max(n, m) + 1)`.
     */
    infix fun add(x: Sbit): Sbit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_sbit(n) - _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_sbit(n) - _sbit(m) = _sbit(max(n, m) + 1)`.
     */
    infix fun sub(x: Sbit): Sbit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width `_sbit(n) * _ubit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width `_sbit(n) * _sbit(m) = _sbit(n + m)`.
     */
    infix fun mul(x: Sbit): Sbit {
        throw VerikDslException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    infix fun sl(x: Int): Sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Arithmetic left shift by [x] bits.
     */
    infix fun sl(x: Ubit): Sbit {
        throw VerikDslException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun sr(x: Int): Sbit {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun sr(x: Ubit): Sbit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit {
        throw VerikDslException()
    }

    /**
     * Reduce bits with and.
     */
    fun red_and(): Boolean {
        throw VerikDslException()
    }

    /**
     * Reduce bits with or.
     */
    fun red_or(): Boolean {
        throw VerikDslException()
    }

    /**
     * Reduce bits with xor.
     */
    fun red_xor(): Boolean {
        throw VerikDslException()
    }

    /**
     * Extend to [width] with sign extension.
     */
    fun ext(width: Int): Sbit {
        throw VerikDslException()
    }

    /**
     * Truncate to [width].
     */
    fun tru(width: Int): Sbit {
        throw VerikDslException()
    }
}

@Suppress("FunctionName")
fun t_Sbit(WIDTH: Int): Sbit {
    throw VerikDslException()
}

/**
 * Constructs a [Sbit] of [WIDTH] and [value].
 */
fun s(WIDTH: Int, value: Int): Sbit {
    return Sbit(WIDTH, value.toString())
}

/**
 * Constructs a [Sbit] of [value]. The width is automatically inferred from the context.
 */
fun s(value: Int): Sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a [Sbit] of [value].
 */
fun s(value: String): Sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a [Sbit] of [value].
 */
fun s(value: Ubit): Sbit {
    throw VerikDslException()
}
