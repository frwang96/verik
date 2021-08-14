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
 * Specify the sensitivity list of a [sequential][Seq] action block.
 * @param x the event to trigger the sequential action block
 * @param block the block to execute when triggered
 */
fun on(x: Event, block: () -> Unit) {
    throw Exception()
}

/**
 * Loop a block forever.
 * @param block the block to loop
 */
fun forever(block: () -> Unit) {
    throw VerikException()
}

/**
 * Delay for some time steps.
 * @param n the number of time steps to delay
 */
fun delay(n: Int) {
    throw VerikException()
}

/**
 * Wait until an event occurs.
 * @param event the event to wait for
 */
fun wait(event: Event) {
    throw VerikException()
}

/**
 * Wait until a clocking block event occurs.
 * @param cb the clocking block to wait for
 */
fun wait(cb: ClockingBlock) {
    throw VerikException()
}