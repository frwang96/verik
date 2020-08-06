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

package io.verik.common.system

import io.verik.common.*
import io.verik.common.data.*

fun time(): _uint {
    throw VerikDslException("function")
}

fun random(): _int {
    throw VerikDslException("function")
}

fun random(size: Int): _sint {
    throw VerikDslException("function")
}

fun urandom(size: Int): _uint {
    throw VerikDslException("function")
}

fun finish() {
    throw VerikDslException("function")
}

fun fatal() {
    throw VerikDslException("function")
}

fun println() {
    throw VerikDslException("function")
}

fun println(message: String) {
    throw VerikDslException("function")
}

fun print(message: String) {
    throw VerikDslException("function")
}
