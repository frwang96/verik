/*
 * Copyright 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER")

package verik.common

import verik.common.data.*

fun on(x: _edge, vararg y: _edge, block: (Unit) -> Unit) {
    throw VerikDslException("function")
}

fun forever(block: (Unit) -> Unit) {
    throw VerikDslException("function")
}

fun repeat(times: _uint, action: (Unit) -> Unit) {
    throw VerikDslException("function")
}

fun repeat(times: _sint, action: (Unit) -> Unit) {
    throw VerikDslException("function")
}

fun repeat(times: Int, action: (Unit) -> Unit) {
    throw VerikDslException("function")
}

fun wait(n: Int = 1) {
    throw VerikDslException("function")
}

fun wait(edge: _edge, n: Int = 1) {
    throw VerikDslException("function")
}
