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

package io.verik.common.data

import io.verik.common.*

infix fun Int.add(x: _sint): _sint {
    throw VerikDslException("function")
}

infix fun Int.add(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.add(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _sint.add(x: _sint): _sint {
    throw VerikDslException("function")
}

infix fun _sint.add(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.add(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.add(x: _sint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.add(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun Int.sub(x: _sint): _sint {
    throw VerikDslException("function")
}

infix fun Int.sub(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.sub(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _sint.sub(x: _sint): _sint {
    throw VerikDslException("function")
}

infix fun _sint.sub(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.sub(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.sub(x: _sint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.sub(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun Int.mul(x: _sint): _sint {
    throw VerikDslException("function")
}

infix fun Int.mul(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.mul(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _sint.mul(x: _sint): _sint {
    throw VerikDslException("function")
}

infix fun _sint.mul(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.mul(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.mul(x: _sint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.mul(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.sl(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _sint.sl(x: _uint): _sint {
    throw VerikDslException("function")
}

infix fun _uint.sl(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.sl(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.sr(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _sint.sr(x: _uint): _sint {
    throw VerikDslException("function")
}

infix fun _uint.sr(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.sr(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.rotl(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _sint.rotl(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.rotl(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.rotl(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.rotr(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _sint.rotr(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _uint.rotr(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _uint.rotr(x: _uint): _uint {
    throw VerikDslException("function")
}

infix fun _sint.sl_ext(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _uint.sl_ext(x: Int): _uint {
    throw VerikDslException("function")
}

infix fun _sint.sr_tru(x: Int): _sint {
    throw VerikDslException("function")
}

infix fun _uint.sr_tru(x: Int): _uint {
    throw VerikDslException("function")
}