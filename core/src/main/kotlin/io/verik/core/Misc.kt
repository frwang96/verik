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
 * Returns an list of [Int] from 0 to [n].
 */
fun range(n: Int): VList<Int> {
    throw VerikException()
}

/**
 * Returns the concatenation of the arguments.
 */
fun cat(vararg x: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns [x] replicated [n] times.
 */
fun rep(n: Int, x: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(vararg x: Int): Int {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(vararg x: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the maximum of the arguments.
 */
fun max(vararg x: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(vararg x: Int): Int {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(vararg x: Sbit<`*`>): Sbit<`*`> {
    throw VerikException()
}

/**
 * Returns the minimum of the arguments.
 */
fun min(vararg x: Ubit<`*`>): Ubit<`*`> {
    throw VerikException()
}

/**
 * Returns the ceiling logarithm base two of [x].
 */
fun log(x: Int): Int {
    throw VerikException()
}

/**
 * Returns the exponential base two of [x].
 */
fun exp(x: Int): Int {
    throw VerikException()
}
