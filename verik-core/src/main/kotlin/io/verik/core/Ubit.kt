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
 * Represents a sequence of [X] bits that is unsigned. Corresponds to SystemVerilog packed logic.
 */
class Ubit<X : `*`> private constructor() {

    /**
     * (UNIMPLEMENTED) Returns true if any bit is X.
     */
    fun isUnknown(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Convert the [Ubit] to type [T].
     */
    fun <T> fromUbit(): T {
        throw VerikException()
    }

    /**
     * Returns this value.
     */
    operator fun unaryPlus(): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Returns the negative of this value.
     */
    operator fun unaryMinus(): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Compares this value with the specified [value]. Returns zero if this value is equal to the specified [value],
     * a negative number if it is less than [value], or a positive number if it is greater than [value].
     */
    operator fun compareTo(value: Ubit<`*`>): Int {
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
     * Insert [value] at offset [index].
     */
    operator fun set(index: Int, value: Ubit<`*`>) {
        throw VerikException()
    }

    /**
     * Insert [value] at offset [index].
     */
    operator fun set(index: Ubit<`*`>, value: Ubit<`*`>) {
        throw VerikException()
    }

    /**
     * Increment by one.
     */
    operator fun inc(): Ubit<X> {
        throw VerikException()
    }

    /**
     * Decrement by one.
     */
    operator fun dec(): Ubit<X> {
        throw VerikException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value without bit growth `Ubit<N> + Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun plus(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value without bit growth `Ubit<N> + Sbit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun plus(value: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with bit growth `Ubit<N> add Ubit<M> = Ubit<MAX<N, M> + 1>`.
     */
    infix fun add(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Add specified [value] to this value with bit growth `Ubit<N> add Sbit<M> = Ubit<MAX<N, M> + 1>`.
     */
    infix fun add(value: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract specified [value] from this value without bit growth `Ubit<N> - Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun minus(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Subtract specified [value] from this value without bit growth * `Ubit<N> - Sbit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun minus(value: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value without bit growth `Ubit<N> * Ubit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun times(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value without bit growth * `Ubit<N> * SBit<M> = Ubit<MAX<N, M>>`.
     */
    operator fun times(value: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with bit growth `Ubit<N> mul Ubit<M> = Ubit<N * M>`.
     */
    infix fun mul(value: Ubit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Multiply specified [value] to this value with bit growth * `Ubit<N> mul Sbit<M> = Ubit<N * M>`.
     */
    infix fun mul(value: Sbit<`*`>): Ubit<`*`> {
        throw VerikException()
    }

    /**
     * Divide specified [value] from this value with width `Ubit<N> / Ubit<M> = Ubit<N>`.
     */
    operator fun div(value: Ubit<`*`>): Ubit<`*`> {
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
    infix fun shl(value: Int): Ubit<X> {
        throw VerikException()
    }

    /**
     * Left shift by [value].
     */
    infix fun shl(value: Ubit<`*`>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun shr(value: Int): Ubit<X> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun shr(value: Ubit<`*`>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun sshr(value: Int): Ubit<X> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun sshr(value: Ubit<`*`>): Ubit<X> {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun invert(): Ubit<X> {
        throw VerikException()
    }

    /**
     * Reverses the bits.
     */
    fun reverse(): Ubit<X> {
        throw VerikException()
    }

    /**
     * Reduce bits with and.
     */
    fun reduceAnd(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with or.
     */
    fun reduceOr(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with xor.
     */
    fun reduceXor(): Boolean {
        throw VerikException()
    }

    /**
     * Returns true if all bits are zero.
     */
    fun isZeroes(): Boolean {
        throw VerikException()
    }

    /**
     * Returns true if all bits are one.
     */
    fun isOnes(): Boolean {
        throw VerikException()
    }

    /**
     * Slice [Y] bits starting at [index].
     */
    fun <Y : `*`> slice(index: Int): Ubit<Y> {
        throw VerikException()
    }

    /**
     * Slice [Y] bits starting at [index].
     */
    fun <Y : `*`> slice(index: Ubit<`*`>): Ubit<Y> {
        throw VerikException()
    }

    /**
     * Extend to [Y] bits with zero extension.
     */
    fun <Y : `*`> ext(): Ubit<Y> {
        throw VerikException()
    }

    /**
     * Extend to [Y] bits with sign extension.
     */
    fun <Y : `*`> sext(): Ubit<Y> {
        throw VerikException()
    }

    /**
     * Truncate to [Y] bits.
     */
    fun <Y : `*`> tru(): Ubit<Y> {
        throw VerikException()
    }
}
