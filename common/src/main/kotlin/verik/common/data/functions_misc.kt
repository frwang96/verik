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

package verik.common.data

import verik.common.*

fun cat(x: _any, vararg y: _any): _uint {
    throw VerikDslException()
}

fun rep(n: _int, x: _bool): _uint {
    throw VerikDslException()
}

fun rep(n: _int, x: _sint): _uint {
    throw VerikDslException()
}

fun rep(n: _int, x: _uint): _uint {
    throw VerikDslException()
}

fun max(x: _sint, y: _sint): _sint {
    throw VerikDslException()
}

fun max(x: _uint, y: _uint): _uint {
    throw VerikDslException()
}

fun max(x: _int, y: _sint): _sint {
    throw VerikDslException()
}

fun max(x: _int, y: _uint): _uint {
    throw VerikDslException()
}

fun max(x: _sint, y: _int): _sint {
    throw VerikDslException()
}

fun max(x: _uint, y: _int): _uint {
    throw VerikDslException()
}

fun min(x: _sint, y: _sint): _sint {
    throw VerikDslException()
}

fun min(x: _uint, y: _uint): _uint {
    throw VerikDslException()
}

fun min(x: _int, y: _sint): _sint {
    throw VerikDslException()
}

fun min(x: _int, y: _uint): _uint {
    throw VerikDslException()
}

fun min(x: _sint, y: _int): _sint {
    throw VerikDslException()
}

fun min(x: _uint, y: _int): _uint {
    throw VerikDslException()
}

fun signed(x: _uint): _sint {
    throw VerikDslException()
}

fun unsigned(x: _sint): _uint {
    throw VerikDslException()
}

fun ext(len: _int, x: _sint): _sint {
    throw VerikDslException()
}

fun ext(len: _int, x: _uint): _uint {
    throw VerikDslException()
}

fun tru(len: _int, x: _sint): _sint {
    throw VerikDslException()
}

fun tru(len: _int, x: _uint): _uint {
    throw VerikDslException()
}