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
 * Exits the simulation with no error status.
 */
fun finish(): Nothing {
    throw VerikDslException()
}

/**
 * Exits the simulation with an error status.
 */
fun fatal(): Nothing {
    throw VerikDslException()
}

/**
 * Prints a string.
 * @param string the string to be printed
 */
fun print(string: String) {
    throw VerikDslException()
}

/**
 * Prints a new line.
 */
fun println() {
    throw VerikDslException()
}

/**
 * Prints a string followed by a new line.
 * @param string the string to be printed
 */
fun println(string: String) {
    throw VerikDslException()
}
