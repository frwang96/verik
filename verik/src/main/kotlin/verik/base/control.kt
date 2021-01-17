/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.base

import verik.data.*

/**
 * Specify the sensitivity list of a [sequential][seq] action block.
 */
fun on(x: _event, vararg y: _event, block: () -> Unit) {
    throw Exception()
}

/**
 * (UNIMPLEMENTED) Give an expression sequential semantics.
 */
fun <T> seq(x: _event, vararg y: _event, block: () -> T): T {
    throw Exception()
}

/**
 * (UNIMPLEMENTED) Give an expression combinational semantics.
 */
fun <T> com(block: () -> T): T {
    throw Exception()
}

/**
 * Loop forever.
 */
fun forever(block: () -> _unit) {
    throw VerikDslException()
}

/**
 * Repeat [n] times.
 */
fun repeat(n: _int, block: () -> _unit) {
    throw VerikDslException()
}

/**
 * Delay for [n] time steps.
 */
fun delay(n: _int) {
    throw VerikDslException()
}

/**
 * Wait until [event] occurs.
 */
fun wait(event: _event) {
    throw VerikDslException()
}

/**
 * Wait until [cport] clock event occurs.
 */
fun wait(cport: _cport) {
    throw VerikDslException()
}
