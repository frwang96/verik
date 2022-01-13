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
 * Represents a sequence of [N] bits that is unsigned. Corresponds to SystemVerilog packed logic.
 */
class Ubit<N : `*`> private constructor() {

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
     * Left shift by [value].
     */
    infix fun shl(value: Int): Ubit<N> {
        throw VerikException()
    }

    /**
     * Left shift by [value].
     */
    infix fun shl(value: Ubit<`*`>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun shr(value: Int): Ubit<N> {
        throw VerikException()
    }

    /**
     * Unsigned right shift by [value].
     */
    infix fun shr(value: Ubit<`*`>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun sshr(value: Int): Ubit<N> {
        throw VerikException()
    }

    /**
     * Signed right shift by [value].
     */
    infix fun sshr(value: Ubit<`*`>): Ubit<N> {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit<N> {
        throw VerikException()
    }

    /**
     * Reverses the bits.
     */
    fun rev(): Ubit<N> {
        throw VerikException()
    }

    /**
     * Reduce bits with and.
     */
    fun andRed(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with or.
     */
    fun orRed(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with xor.
     */
    fun xorRed(): Boolean {
        throw VerikException()
    }

    /**
     * Returns true if all bits are zero.
     */
    fun eqz(): Boolean {
        throw VerikException()
    }

    /**
     * Returns true if not all bits are zero.
     */
    fun neqz(): Boolean {
        throw VerikException()
    }

    /**
     * Slice [M] bits starting at [index].
     */
    fun <M : `*`> sli(index: Int): Ubit<M> {
        throw VerikException()
    }

    /**
     * Slice [M] bits starting at [index].
     */
    fun <M : `*`> sli(index: Ubit<`*`>): Ubit<M> {
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
    fun <M : `*`> sext(): Ubit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M : `*`> tru(): Ubit<M> {
        throw VerikException()
    }

    /**
     * Resize to [M] bits with zero extension. This will extend to [M] bits with zero extension if [M] is larger than
     * [N] and truncate to [M] bits otherwise. It should only be used if it is not known beforehand whether the value
     * will be extended or truncated.
     */
    fun <M : `*`> res(): Ubit<M> {
        throw VerikException()
    }

    /**
     * Resize to [M] bits with sign extension. This will extend to [M] bits with sign extension if [M] is larger than
     * [N] and truncate to [M] bits otherwise. It should only be used if it is not known beforehand whether the value
     * will be extended or truncated.
     */
    fun <M : `*`> sres(): Ubit<M> {
        throw VerikException()
    }
}
