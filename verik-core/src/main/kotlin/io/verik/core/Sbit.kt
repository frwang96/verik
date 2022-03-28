/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "unused")

package io.verik.core

/**
 * Represents a sequence of [N] bits that is signed. It corresponds to SystemVerilog packed signed logic.
 */
class Sbit<N : `*`> private constructor() {

    /**
     * (UNIMPLEMENTED) Returns true if any bit is unknown.
     */
    fun hasx(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if any bit is floating.
     */
    fun hasz(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if any bit is unknown or floating.
     */
    fun hasxz(): Boolean {
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
     * Extend to [M] bits with sign extension.
     */
    fun <M : `*`> ext(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Extend to [M] bits with zero extension.
     */
    fun <M : `*`> uext(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Truncate to [M] bits.
     */
    fun <M : `*`> tru(): Sbit<M> {
        throw VerikException()
    }

    /**
     * Converts this value to a [Ubit].
     */
    fun toUbit(): Ubit<N> {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Converts this value to an [Int].
     */
    fun toInt(): Int {
        throw VerikException()
    }
}
