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
 * @param x the event to trigger the sequential action block
 * @param y the additional events to trigger the sequential action block
 * @param block the block to execute when triggered
 */
fun on(x: Event, vararg y: Event, block: () -> Unit) {
    throw Exception()
}

/**
 * Loop a block forever.
 * @param block the block to loop
 */
fun forever(block: () -> Unit) {
    throw VerikDslException()
}

/**
 * Repeat a block a number of times.
 * @param n the number of times to loop
 * @param block the block to loop
 */
fun repeat(n: Int, block: () -> Unit) {
    throw VerikDslException()
}

/**
 * Delay for some time steps.
 * @param n the number of time steps to delay
 */
fun delay(n: Int) {
    throw VerikDslException()
}

/**
 * Wait until an event occurs.
 * @param event the event to wait for
 */
fun wait(event: Event) {
    throw VerikDslException()
}

/**
 * Wait until a clock port event occurs.
 * @param cp the clock port to wait for
 */
fun wait(cp: ClockPort) {
    throw VerikDslException()
}
