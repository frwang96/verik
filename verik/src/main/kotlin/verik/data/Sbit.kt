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
 * Represents a sequence of bits that is signed. Corresponds to SystemVerilog packed signed logic.
 * @property O the width in bits
 */
class Sbit<O: Ordinal> private constructor(): Logic() {

//////////////////////////////////////////////////////////////////////////////// BUILD
    /**
     * Pack the [Sbit] as a [Ubit].
     */
    @Deprecated("UNIMPLEMENTED")
    fun pack(): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Returns true if any bit of the [Sbit] is unknown.
     */
    @Deprecated("UNIMPLEMENTED")
    fun is_unknown(): Boolean {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    operator fun unaryPlus(): Sbit<Ordinal> {
        throw VerikDslException()
    }

    operator fun unaryMinus(): Sbit<Ordinal> {
        throw VerikDslException()
    }

    operator fun compareTo(x: Sbit<Ordinal>): Int {
        throw VerikDslException()
    }

    /**
     * Get bit [n].
     */
    operator fun get(n: Int): Boolean {
        throw VerikDslException()
    }

    /**
     * Get bit [n].
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Ubit<Ordinal>): Boolean {
        throw VerikDslException()
    }

    /**
     * Set bit [n].
     */
    operator fun set(n: Int, value: Boolean) {
        throw VerikDslException()
    }

    /**
     * Set bit [n].
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun set(n: Ubit<Ordinal>, value: Boolean) {
        throw VerikDslException()
    }

    /**
     * Increment by one.
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun inc(): Sbit<O> {
        throw VerikDslException()
    }

    /**
     * Decrement by one.
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun dec(): Sbit<O> {
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
    operator fun plus(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_sbit(n) + _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun plus(x: Sbit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_sbit(n) - _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_sbit(n) - _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun minus(x: Sbit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_sbit(n) * _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_sbit(n) * _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun times(x: Sbit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width `_sbit(n) + _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width `_sbit(n) + _sbit(m) = _sbit(max(n, m) + 1)`.
     */
    infix fun add(x: Sbit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_sbit(n) - _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_sbit(n) - _sbit(m) = _sbit(max(n, m) + 1)`.
     */
    infix fun sub(x: Sbit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width `_sbit(n) * _ubit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width `_sbit(n) * _sbit(m) = _sbit(n + m)`.
     */
    infix fun mul(x: Sbit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    infix fun shl(x: Int): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    @Deprecated("UNIMPLEMENTED")
    infix fun shl(x: Ubit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun shr(x: Int): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    @Deprecated("UNIMPLEMENTED")
    infix fun shr(x: Ubit<Ordinal>): Sbit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Sbit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Sbit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Ubit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Sbit<Ordinal>): Ubit<Ordinal> {
        throw VerikDslException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit<Ordinal> {
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
     * Slice [O] bits starting at n.
     */
    fun <O: Ordinal> slice(n: Int): Ubit<O> {
        throw VerikDslException()
    }

    /**
     * Slice [O] bits starting at n.
     */
    fun <O: Ordinal> slice(n: Ubit<Ordinal>): Ubit<O> {
        throw VerikDslException()
    }

    /**
     * Extend to [O] bits with sign extension.
     */
    fun <O: Ordinal> ext(): Sbit<O> {
        throw VerikDslException()
    }

    /**
     * Truncate to [O] bits.
     */
    fun <O: Ordinal> tru(): Sbit<O> {
        throw VerikDslException()
    }
}

/**
 * Constructs a [Sbit].
 * @param O the width in bits
 * @param value the value
 */
fun <O: Ordinal> s(value: Int): Sbit<O> {
    throw VerikDslException()
}

/**
 * Constructs a [Sbit].
 * @param value the value
 */
fun s(value: Ubit<Ordinal>): Sbit<Ordinal> {
    throw VerikDslException()
}
