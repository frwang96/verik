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

infix fun _int.add(x: _sint): _sint {
    throw VerikDslException()
}

infix fun _int.add(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.add(x: _int): _sint {
    throw VerikDslException()
}

infix fun _sint.add(x: _sint): _sint {
    throw VerikDslException()
}

infix fun _sint.add(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.add(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.add(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.add(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.sub(x: _sint): _sint {
    throw VerikDslException()
}

infix fun _int.sub(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.sub(x: _int): _sint {
    throw VerikDslException()
}

infix fun _sint.sub(x: _sint): _sint {
    throw VerikDslException()
}

infix fun _sint.sub(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.sub(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.sub(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.sub(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.mul(x: _sint): _sint {
    throw VerikDslException()
}

infix fun _int.mul(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.mul(x: _int): _sint {
    throw VerikDslException()
}

infix fun _sint.mul(x: _sint): _sint {
    throw VerikDslException()
}

infix fun _sint.mul(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.mul(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.mul(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.mul(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.sl(x: _int): _sint {
    throw VerikDslException()
}

infix fun _sint.sl(x: _uint): _sint {
    throw VerikDslException()
}

infix fun _uint.sl(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.sl(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.sr(x: _int): _sint {
    throw VerikDslException()
}

infix fun _sint.sr(x: _uint): _sint {
    throw VerikDslException()
}

infix fun _uint.sr(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.sr(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.rotl(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.rotl(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.rotl(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.rotl(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.rotr(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.rotr(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.rotr(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.rotr(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.sl_ext(x: _int): _sint {
    throw VerikDslException()
}

infix fun _uint.sl_ext(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.sr_tru(x: _int): _sint {
    throw VerikDslException()
}

infix fun _uint.sr_tru(x: _int): _uint {
    throw VerikDslException()
}