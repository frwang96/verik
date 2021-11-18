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
class Sbit<N : `*`> private constructor() {

    /**
     * Returns true if any bit of the [Sbit] is unknown.
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
     * Add specified [value] to this value with truncated width `Sbit<N> + Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun plus(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with truncated width `Sbit<N> + Sbit<M> = Sbit<MAX<N, M>>`.
     */
    operator fun plus(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract specified [value] from this value with truncated width `Sbit<N> - Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun minus(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract specified [value] from this value with truncated width `Sbit<N> - Sbit<M> = Sbit<MAX<N, M>>`.
     */
    operator fun minus(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with truncated width `Sbit<N> * Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun times(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with truncated width `Sbit<N> * Sbit<M> = Sbit<MAX<N, M>>`.
     */
    operator fun times(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with full width `Sbit<N> + Ubit<M> = Ubit<MAX<N, M> + 1>`.
     */
    infix fun add(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with full width `Sbit<N> + Sbit<M> = Sbit<MAX<N, M> + 1>`.
     */
    infix fun add(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with full width `Sbit<N> * Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with full width `Sbit<N> * Sbit<M> = Sbit<N * M>`.
     */
    infix fun mul(value: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Left shift by [value].
     */
    infix fun shl(value: Int): Sbit<N> {
        throw VerikException()
    }

    /**
     * Left shift by [value].
     */
    infix fun shl(value: Ubit<`*`>): Sbit<N> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun shr(value: Int): Sbit<N> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun shr(value: Ubit<`*`>): Sbit<N> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun ushr(value: Int): Sbit<N> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun ushr(value: Ubit<`*`>): Sbit<N> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and with [value].
     */
    infix fun and(value: Ubit<N>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and with [value].
     */
    infix fun and(value: Sbit<N>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or with [value].
     */
    infix fun or(value: Ubit<N>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or with [value].
     */
    infix fun or(value: Sbit<N>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor with [value].
     */
    infix fun xor(value: Ubit<N>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor with [value].
     */
    infix fun xor(value: Sbit<N>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit<N> {
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
     * Slice [M] bits starting at [index].
     */
    fun <M : `*`> slice(index: Int): Ubit<M> {
        throw VerikException()
    }

    /**
     * Slice [M] bits starting at [index].
     */
    fun <M : `*`> slice(index: Ubit<`*`>): Ubit<M> {
        throw VerikException()
    }

    /**
     * Extend to [M] bits with sign extension.
     */
    fun <M : `*`> ext(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Extend to [M] bits with zero extension.
     */
    fun <M : `*`> uext(): Ubit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M : `*`> tru(): Sbit<M> {
        throw VerikException()
    }
}
