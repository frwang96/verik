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
 * Returns the value of the logical [X] as a boolean.
 */
fun <X : `?`> b(): Boolean {
    throw VerikException()
}

/**
 * Returns the value of cardinal [N] as an integer.
 */
fun <N : `*`> i(): Int {
    throw VerikException()
}

/**
 * Constructs a [Ubit] out of the constant [value].
 */
fun u(value: Int): Ubit<Cardinal> {
    throw VerikException()
}

/**
 * Constructs a [Ubit] out of the cardinal [N].
 */
fun <N: `*`> u(): Ubit<Cardinal> {
    throw VerikException()
}

/**
 * Constructs a [Sbit] out of the constant [value].
 */
fun s(value: Int): Sbit<Cardinal> {
    throw VerikException()
}
/**
 * Returns a value of type [T] with all bits set to zero.
 */
fun <T> zeroes(): T {
    throw VerikException()
}

/**
 * Returns a value of type [T] with all bits set to one.
 */
fun <T> ones(): T {
    throw VerikException()
}

/**
 * Returns a value of type [T] with all bits set to X.
 */
fun <T> x(): T {
    throw VerikException()
}

/**
 * Returns a value of type [T] with all bits set to Z.
 */
fun <T> z(): T {
    throw VerikException()
}

/**
 * Represents a value that is not connected in output port connections or property declarations.
 */
fun <T> nc(): T {
    throw VerikException()
}

/**
 * Inject [content] as SystemVerilog to use unsupported language constructs.
 */
fun sv(content: String) {
    throw VerikException()
}
