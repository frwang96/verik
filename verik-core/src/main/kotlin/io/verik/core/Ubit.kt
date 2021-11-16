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
class Ubit<N : `*`> private constructor() {

    /**
     * Returns true if any bit of the [Ubit] is unknown.
     */
    fun isUnknown(): Boolean {
        throw VerikException()
    }

    operator fun unaryPlus(): Ubit<`*`> {
        throw VerikException()
    }

    operator fun unaryMinus(): Ubit<`*`> {
        throw VerikException()
    }

    operator fun compareTo(x: Ubit<`*`>): Int {
        throw VerikException()
    }

    /**
     * Convert the [Ubit] to type [T].
     */
    fun <T> fromUbit(): T {
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
     * Set [value] at offset [n].
     */
    operator fun set(n: Int, value: Ubit<`*`>) {
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
     * Add [x] to this value with truncated width `Ubit<N> + Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun plus(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `Ubit<N> + Sbit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun plus(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Ubit<N> - Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun minus(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `Ubit<N> - Sbit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun minus(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Ubit<N> * Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun times(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `Ubit<N> * SBit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun times(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Divide [x] from this value with width `Ubit<N> / Ubit<M> = Ubit<N>`.
     */
    operator fun div(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Ubit<N> + Ubit<M> = Ubit<MAX<N, M> + 1>`.
     */
    infix fun add(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `Ubit<N> + Sbit<M> = Ubit<MAX<N, M> + 1>`.
     */
    infix fun add(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Ubit<N> * Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `Ubit<N> * Sbit<M> = Ubit<N * M>`.
     */
    infix fun mul(x: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Left shift by [x] bits.
     */
    infix fun shl(x: Int): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Left shift by [x] bits.
     */
    infix fun shl(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [x] bits.
     */
    infix fun shr(x: Int): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [x] bits.
     */
    infix fun shr(x: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Signed right shift by [x] bits.
     */
    infix fun sshr(x: Int): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Signed right shift by [x] bits.
     */
    infix fun sshr(x: Ubit<`*`>): Ubit<`*`> {
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
     * Fills in [value] at bit [n].
     */
    fun fill(n: Int, value: Boolean): Ubit<N> {
        throw VerikException()
    }

    /**
     * Fills in [value] starting at bit [n].
     */
    fun fill(n: Int, value: Ubit<`*`>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun invert(): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Reverses the bits.
     */
    fun reverse(): Ubit<`*`> {
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
     * Extend to [M] bits with zero extension.
     */
    fun <M : `*`> ext(): Ubit<M> {
        throw VerikException()
    }

    /**
     * Extend to [M] bits with sign extension.
     */
    fun <M : `*`> sext(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M : `*`> tru(): Ubit<M> {
        throw VerikException()
    }
}
