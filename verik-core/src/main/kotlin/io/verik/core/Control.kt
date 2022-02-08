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
 * Returns an event for a positive edge transition of [value].
 */
fun posedge(value: Boolean): Event {
    throw VerikException()
}

/**
 * Returns an event for a negative edge transition of [value].
 */
fun negedge(value: Boolean): Event {
    throw VerikException()
}

/**
 * Trigger a [sequential][Seq] action block on event [event] and optionally [additional_events].
 *
 *  ```
 *  @Seq
 *  fun f() {
 *      on(posedge(clk)) {
 *          x = !y
 *      }
 *  }
 *  ```
 */
fun on(event: Event, vararg additional_events: Event, block: () -> Unit) {
    throw Exception()
}

/**
 * Inline form of [on]. Trigger a [sequentially][Seq] assigned property on event [event] and optionally
 * [additional_events].
 *
 *  ```
 *  @Seq
 *  var x = onr(posedge(clk)) { !y }
 *  ```
 */
fun <T> oni(event: Event, vararg additional_events: Event, block: () -> T): T {
    throw Exception()
}

/**
 * Loop [action] forever.
 *
 *  ```
 *  forever {
 *      clk = !clk
 *      delay(1)
 *  }
 *  ```
 */
fun forever(action: () -> Unit) {
    throw VerikException()
}

/**
 * Delay for [value] time steps.
 */
fun delay(value: Int) {
    throw VerikException()
}

/**
 * Wait until [value] is true.
 */
fun wait(value: Boolean) {
    throw VerikException()
}

/**
 * Wait until event [event] occurs.
 */
fun wait(event: Event) {
    throw VerikException()
}

/**
 * Wait until clocking block event occurs.
 */
fun wait(clocking_block: ClockingBlock) {
    throw VerikException()
}

/**
 * Spawn the process [action] and execute it concurrently with the parent process.
 * To wait until all immediate child processes have terminated use [join].
 *
 *  ```
 *  fork { delay(10) }
 *  join()
 *  ```
 */
fun fork(action: () -> Unit) {
    throw VerikException()
}

/**
 * Wait until all immediate child processes spawned by [fork] have terminated.
 */
fun join() {
    throw VerikException()
}
