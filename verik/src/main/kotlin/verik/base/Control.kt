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

/**
 * Specify the sensitivity list of a [sequential][seq] action block.
 */
fun on(x: Event, vararg y: Event, block: () -> Unit) {
    throw Exception()
}

/**
 * (UNIMPLEMENTED) Give an expression sequential semantics.
 */
fun <T> seq(x: Event, vararg y: Event, block: () -> T): T {
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
fun forever(block: () -> Unit) {
    throw VerikDslException()
}

/**
 * Repeat [n] times.
 */
fun repeat(n: Int, block: () -> Unit) {
    throw VerikDslException()
}

/**
 * Delay for [n] time steps.
 */
fun delay(n: Int) {
    throw VerikDslException()
}

/**
 * Wait until [event] occurs.
 */
fun wait(event: Event) {
    throw VerikDslException()
}

/**
 * Wait until [cp] clock event occurs.
 */
fun wait(cp: ClockPort) {
    throw VerikDslException()
}
