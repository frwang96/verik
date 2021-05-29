/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.core

/**
 * Represents a sequence of bits that is signed. Corresponds to SystemVerilog packed signed logic.
 * @property N the width in bits
 */
class Sbit<N: Cardinal> private constructor() {

    /**
     * Pack the [Sbit] as a [Ubit].
     */
    fun pack(): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Returns true if any bit of the [Sbit] is unknown.
     */
    fun isUnknown(): Boolean {
        throw VerikException()
    }

    operator fun unaryPlus(): Sbit<Cardinal> {
        throw VerikException()
    }

    operator fun unaryMinus(): Sbit<Cardinal> {
        throw VerikException()
    }

    operator fun compareTo(x: Sbit<Cardinal>): Int {
        throw VerikException()
    }

    /**
     * Get bit [n].
     */
    operator fun get(n: Int): Boolean {
        throw VerikException()
    }

    /**
     * Get bit [n].
     */
    operator fun get(n: Ubit<Cardinal>): Boolean {
        throw VerikException()
    }

    /**
     * Set bit [n].
     */
    operator fun set(n: Int, value: Boolean) {
        throw VerikException()
    }

    /**
     * Set bit [n].
     */
    operator fun set(n: Ubit<Cardinal>, value: Boolean) {
        throw VerikException()
    }

    /**
     * Increment by one.
     */
    operator fun inc(): Sbit<N> {
        throw VerikException()
    }

    /**
     * Decrement by one.
     */
    operator fun dec(): Sbit<N> {
        throw VerikException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `Sbit<N> + Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun plus(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `Sbit<N> + Sbit<M> = Sbit<MAX<n, m>>`.
     */
    operator fun plus(x: Sbit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Sbit<N> - Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun minus(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Sbit<N> - Sbit<M> = Sbit<MAX<n, m>>`.
     */
    operator fun minus(x: Sbit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Sbit<N> * Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun times(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Sbit<N> * Sbit<M> = Sbit<MAX<n, m>>`.
     */
    operator fun times(x: Sbit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Sbit<N> + Ubit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun add(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Sbit<N> + Sbit<M> = Sbit<MAX<n, m> + 1>`.
     */
    infix fun add(x: Sbit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `Sbit<N> - Ubit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun sub(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `Sbit<N> - Sbit<M> = Sbit<MAX<n, m> + 1>`.
     */
    infix fun sub(x: Sbit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Sbit<N> * Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Sbit<N> * Sbit<M> = Sbit<N * M>`.
     */
    infix fun mul(x: Sbit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    infix fun shl(x: Int): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    infix fun shl(x: Ubit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun shr(x: Int): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun shr(x: Ubit<Cardinal>): Sbit<Cardinal> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Reduce bits with and.
     */
    fun redAnd(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with or.
     */
    fun redOr(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with xor.
     */
    fun redXor(): Boolean {
        throw VerikException()
    }

    /**
     * Slice [M] bits starting at n.
     */
    fun <M: Cardinal> slice(n: Int): Ubit<M> {
        throw VerikException()
    }

    /**
     * Slice [M] bits starting at n.
     */
    fun <M: Cardinal> slice(n: Ubit<Cardinal>): Ubit<M> {
        throw VerikException()
    }

    /**
     * Extend to [M] bits with sign extension.
     */
    fun <M: Cardinal> ext(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M: Cardinal> tru(): Sbit<M> {
        throw VerikException()
    }
}

/**
 * Constructs a [Sbit].
 * @param N the width in bits
 * @param value the value
 */
fun <N: Cardinal> s(value: Int): Sbit<N> {
    throw VerikException()
}

/**
 * Constructs a [Sbit].
 * @param value the value
 */
fun s(value: Ubit<Cardinal>): Sbit<Cardinal> {
    throw VerikException()
}
