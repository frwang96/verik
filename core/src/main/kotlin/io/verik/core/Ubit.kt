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
 * Represents an sequence of bits that is unsigned. Corresponds to SystemVerilog packed logic.
 * @param N the width in bits
 */
class Ubit<N: Cardinal> private constructor() {

    /**
     * Returns true if any bit of the [Ubit] is unknown.
     */
    fun isUnknown(): Boolean {
        throw VerikException()
    }

    operator fun unaryPlus(): Ubit<Cardinal> {
        throw VerikException()
    }

    operator fun unaryMinus(): Ubit<Cardinal> {
        throw VerikException()
    }

    operator fun compareTo(x: Ubit<Cardinal>): Int {
        throw VerikException()
    }

    /**
     * Unpack the [Ubit] to a particular type.
     * @param T the type to unpack to
     */
    fun <T> unpack(): T {
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
    operator fun inc(): Ubit<N> {
        throw VerikException()
    }

    /**
     * Decrement by one.
     */
    operator fun dec(): Ubit<N> {
        throw VerikException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `Ubit<N> + Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun plus(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `Ubit<N> + Sbit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun plus(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Ubit<N> - Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun minus(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Ubit<N> - Sbit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun minus(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Ubit<N> * Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun times(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Ubit<N> * SBit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun times(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Ubit<N> + Ubit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun add(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Ubit<N> + Sbit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun add(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `Ubit<N> - Ubit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun sub(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `Ubit<N> - Sbit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun sub(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Ubit<N> * Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Ubit<N> * Sbit<M> = Ubit<N * M>`.
     */
    infix fun mul(x: Sbit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    infix fun shl(x: Int): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    infix fun shl(x: Ubit<Cardinal>): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Logical right shift by [x] bits.
     */
    infix fun shr(x: Int): Ubit<Cardinal> {
        throw VerikException()
    }

    /**
     * Logical right shift by [x] bits.
     */
    infix fun shr(x: Ubit<Cardinal>): Ubit<Cardinal> {
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
     * Extend to [M] bits with zero extension.
     */
    fun <M: Cardinal> ext(): Ubit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M: Cardinal> tru(): Ubit<M> {
        throw VerikException()
    }
}

/**
 * Constructs a [Ubit].
 * @param N the width in bits
 * @param value the value
 */
fun <N: Cardinal> u(value: Int): Ubit<N> {
    throw VerikException()
}

/**
 * Constructs a [Ubit].
 * @param value the value
 */
fun u(value: Sbit<Cardinal>): Ubit<Cardinal> {
    throw VerikException()
}
