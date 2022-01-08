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
 * Represents a sequence of [X] bits that is signed. Corresponds to SystemVerilog packed signed logic.
 */
class Sbit<X : `*`> private constructor() {

    /**
     * (UNIMPLEMENTED) Returns true if any bit of the [Sbit] is unknown.
     */
    fun isUnknown(): Boolean {
        throw VerikException()
    }

    /**
     * Returns this value.
     */
    operator fun unaryPlus(): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Returns the negative of this value.
     */
    operator fun unaryMinus(): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Compares this value with the specified [value]. Returns zero if this value is equal to the specified [value],
     * a negative number if it is less than [value], or a positive number if it is greater than [value].
     */
    operator fun compareTo(value: Sbit<`*`>): Int {
        throw VerikException()
    }

    /**
     * Get bit at [index].
     */
    operator fun get(index: Int): Boolean {
        throw VerikException()
    }

    /**
     * Get bit at [index].
     */
    operator fun get(index: Ubit<`*`>): Boolean {
        throw VerikException()
    }

    /**
     * Set bit at [index] to [value].
     */
    operator fun set(index: Int, value: Boolean) {
        throw VerikException()
    }

    /**
     * Set bit at [index] to [value].
     */
    operator fun set(index: Ubit<`*`>, value: Boolean) {
        throw VerikException()
    }

    /**
     * Increment by one.
     */
    operator fun inc(): Sbit<X> {
        throw VerikException()
    }

    /**
     * Decrement by one.
     */
    operator fun dec(): Sbit<X> {
        throw VerikException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value without bit growth `Sbit<N> + Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun plus(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value without bit growth `Sbit<N> + Sbit<M> = Sbit<MAX<N, M>>`.
     */
    operator fun plus(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with bit growth `Sbit<N> add Ubit<M> = Ubit<MAX<N, M> + 1>`.
     */
    infix fun add(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with bit growth `Sbit<N> add Sbit<M> = Sbit<MAX<N, M> + 1>`.
     */
    infix fun add(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract specified [value] from this value without bit growth * `Sbit<N> - Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun minus(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract specified [value] from this value without bit growth * `Sbit<N> - Sbit<M> = Sbit<MAX<N, M>>`.
     */
    operator fun minus(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value without bit growth * `Sbit<N> * Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun times(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value without bit growth * `Sbit<N> * Sbit<M> = Sbit<MAX<N, M>>`.
     */
    operator fun times(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with bit growth `Sbit<N> mul Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with bit growth `Sbit<N> mul Sbit<M> = Sbit<N * M>`.
     */
    infix fun mul(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and with [value].
     */
    infix fun and(value: Ubit<X>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and with [value].
     */
    infix fun and(value: Sbit<X>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or with [value].
     */
    infix fun or(value: Ubit<X>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or with [value].
     */
    infix fun or(value: Sbit<X>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor with [value].
     */
    infix fun xor(value: Ubit<X>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor with [value].
     */
    infix fun xor(value: Sbit<X>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Left shift by [value].
     */
    infix fun shl(value: Int): Sbit<X> {
        throw VerikException()
    }

    /**
     * Left shift by [value].
     */
    infix fun shl(value: Ubit<`*`>): Sbit<X> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun shr(value: Int): Sbit<X> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun shr(value: Ubit<`*`>): Sbit<X> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun ushr(value: Int): Sbit<X> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun ushr(value: Ubit<`*`>): Sbit<X> {
        throw VerikException()
    }

    /**
     * Extend to [Y] bits with sign extension.
     */
    fun <Y : `*`> ext(): Sbit<Y> {
        throw VerikException()
    }

    /**
     * Extend to [Y] bits with zero extension.
     */
    fun <Y : `*`> uext(): Sbit<Y> {
        throw VerikException()
    }

    /**
     * Truncate to [Y] bits.
     */
    fun <Y : `*`> tru(): Sbit<Y> {
        throw VerikException()
    }
}
