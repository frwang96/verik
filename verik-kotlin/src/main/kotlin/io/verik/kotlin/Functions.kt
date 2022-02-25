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

package io.verik.kotlin

/**
 * Executes the given function [action] specified number of [times]. A zero-based index of current iteration is passed
 * as a parameter to [action].
 */
fun repeat(times: Int, action: (Int) -> Unit) {
    throw VerikException()
}

/**
 * Asserts that [value] is true. Raises an error otherwise.
 */
fun assert(value: Boolean) {
    throw VerikException()
}

/**
 * Asserts that [value] is true. Executes [action] otherwise.
 */
fun assert(value: Boolean, action: () -> Any) {
    throw VerikException()
}
