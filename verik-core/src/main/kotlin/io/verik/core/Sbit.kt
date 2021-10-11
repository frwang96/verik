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
     * Convert the [Sbit] to a [Ubit].
     */
    fun toUbit(): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Returns true if any bit of the [Sbit] is unknown.
     */
    fun isUnknown(): Boolean {
        throw VerikException()
    }

    operator fun unaryPlus(): Sbit<`*`> {
        throw VerikException()
    }

    operator fun unaryMinus(): Sbit<`*`> {
        throw VerikException()
    }

    operator fun compareTo(x: Sbit<`*`>): Int {
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
    operator fun get(n: Ubit<`*`>): Boolean {
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
    operator fun set(n: Ubit<`*`>, value: Boolean) {
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
    operator fun plus(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `Sbit<N> + Sbit<M> = Sbit<MAX<n, m>>`.
     */
    operator fun plus(x: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Sbit<N> - Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun minus(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Sbit<N> - Sbit<M> = Sbit<MAX<n, m>>`.
     */
    operator fun minus(x: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Sbit<N> * Ubit<M> = Ubit<MAX<n, m>>`.
     */
    operator fun times(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Sbit<N> * Sbit<M> = Sbit<MAX<n, m>>`.
     */
    operator fun times(x: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Sbit<N> + Ubit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun add(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Sbit<N> + Sbit<M> = Sbit<MAX<n, m> + 1>`.
     */
    infix fun add(x: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `Sbit<N> - Ubit<M> = Ubit<MAX<n, m> + 1>`.
     */
    infix fun sub(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `Sbit<N> - Sbit<M> = Sbit<MAX<n, m> + 1>`.
     */
    infix fun sub(x: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Sbit<N> * Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Sbit<N> * Sbit<M> = Sbit<N * M>`.
     */
    infix fun mul(x: Sbit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    infix fun sll(x: Int): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    infix fun sll(x: Ubit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Logical right shift by [x] bits with zero extension.
     */
    infix fun srl(x: Int): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Logical right shift by [x] bits with zero extension.
     */
    infix fun srl(x: Ubit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun sra(x: Int): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun sra(x: Ubit<`*`>): Sbit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit<`*`> {
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
    fun <M : `*`> slice(n: Int): Ubit<M> {
        throw VerikException()
    }

    /**
     * Slice [M] bits starting at n.
     */
    fun <M : `*`> slice(n: Ubit<`*`>): Ubit<M> {
        throw VerikException()
    }

    /**
     * Extend to [M] bits with sign extension.
     */
    fun <M : `*`> ext(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M : `*`> tru(): Sbit<M> {
        throw VerikException()
    }
}
