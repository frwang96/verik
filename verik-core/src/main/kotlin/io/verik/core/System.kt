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
 * Exits the simulation with no error status.
 */
fun finish(): Nothing {
    throw VerikException()
}
/**
 * Exits the simulation with error status.
 */
fun fatal(): Nothing {
    throw VerikException()
}

/**
 * Logs [message] with severity fatal and exits the simulation with error status.
 */
fun fatal(message: String): Nothing {
    throw VerikException()
}

/**
 * Logs [message] with severity error.
 */
fun error(message: String) {
    throw VerikException()
}

/**
 * Logs [message] with severity warning.
 */
fun warning(message: String) {
    throw VerikException()
}

/**
 * Logs [message] with severity info.
 */
fun info(message: String) {
    throw VerikException()
}
