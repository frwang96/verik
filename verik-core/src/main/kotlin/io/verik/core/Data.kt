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

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Returns the value of the cardinal [X] as a [Boolean]. [X] must take the value of either zero or one.
 */
fun <X : `*`> b(): Boolean {
    throw VerikException()
}

/**
 * Returns the value of cardinal [N] as an integer.
 */
fun <N : `*`> i(): Int {
    throw VerikException()
}

/**
 * Construct a [Ubit] out of the cardinal [N].
 */
fun <N : `*`> u(): Ubit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Ubit] out of the constant [value].
 */
fun u(value: Int): Ubit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Ubit] out of the constant [value].
 */
fun u(value: String): Ubit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to false.
 */
fun <N : `*`> u0(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to true.
 */
fun <N : `*`> u1(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to unknown.
 */
fun <N : `*`> ux(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to floating.
 */
fun <N : `*`> uz(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Sbit] out of the constant [value].
 */
fun s(value: Int): Sbit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Sbit] out of the constant [value].
 */
fun s(value: String): Sbit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Sbit] with all bits set to false.
 */
fun <N : `*`> s0(): Sbit<N> {
    throw VerikException()
}

/**
 * Construct a [Sbit] with all bits set to true.
 */
fun <N : `*`> s1(): Sbit<N> {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Construct a [Sbit] with all bits set to unknown.
 */
fun <N : `*`> sx(): Sbit<N> {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Construct a [Sbit] with all bits set to floating.
 */
fun <N : `*`> sz(): Sbit<N> {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to false.
 */
fun <T> fill0(): T {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to true.
 */
fun <T> fill1(): T {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to unknown.
 */
fun <T> fillx(): T {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to floating.
 */
fun <T> fillz(): T {
    throw VerikException()
}

/**
 * Unknown boolean value.
 */
val unknown: Boolean by VerikExceptionDelegate()

/**
 * Floating boolean value.
 */
val floating: Boolean by VerikExceptionDelegate()
