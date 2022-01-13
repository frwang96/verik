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
 * Returns the concatenation of the arguments.
 */
fun cat(value: Any, vararg additionalValues: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns [value] replicated [N] times.
 */
fun <N : `*`> rep(value: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(value: Int, vararg additionalValues: Int): Int {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(value: Ubit<`*`>, vararg additionalValues: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(value: Sbit<`*`>, vararg additionalValues: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(value: Int, vararg additionalValues: Int): Int {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(value: Ubit<`*`>, vararg additionalValues: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(value: Sbit<`*`>, vararg additionalValues: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the ceiling logarithm base two of [value].
 */
fun log(value: Int): Int {
    throw VerikException()
}

/**
 * Returns the exponential base two of [value].
 */
fun exp(value: Int): Int {
    throw VerikException()
}
