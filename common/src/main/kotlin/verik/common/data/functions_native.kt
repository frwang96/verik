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

operator fun _sint.unaryPlus(): _sint {
    throw VerikDslException()
}

operator fun _uint.unaryPlus(): _uint {
    throw VerikDslException()
}

operator fun _sint.unaryMinus(): _sint {
    throw VerikDslException()
}

operator fun _uint.unaryMinus(): _uint {
    throw VerikDslException()
}

operator fun _sint.inc(): _sint {
    throw VerikDslException()
}

operator fun _uint.inc(): _uint {
    throw VerikDslException()
}

operator fun _sint.dec(): _sint {
    throw VerikDslException()
}

operator fun _uint.dec(): _uint {
    throw VerikDslException()
}

operator fun _sint.not(): _bool {
    throw VerikDslException()
}

operator fun _uint.not(): _bool {
    throw VerikDslException()
}

operator fun _int.plus(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _int.plus(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _sint.plus(x: _int): _sint {
    throw VerikDslException()
}

operator fun _sint.plus(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _sint.plus(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _uint.plus(x: _int): _uint {
    throw VerikDslException()
}

operator fun _uint.plus(x: _sint): _uint {
    throw VerikDslException()
}

operator fun _uint.plus(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _int.minus(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _int.minus(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _sint.minus(x: _int): _sint {
    throw VerikDslException()
}

operator fun _sint.minus(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _sint.minus(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _uint.minus(x: _int): _uint {
    throw VerikDslException()
}

operator fun _uint.minus(x: _sint): _uint {
    throw VerikDslException()
}

operator fun _uint.minus(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _int.times(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _int.times(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _sint.times(x: _int): _sint {
    throw VerikDslException()
}

operator fun _sint.times(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _sint.times(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _uint.times(x: _int): _uint {
    throw VerikDslException()
}

operator fun _uint.times(x: _sint): _uint {
    throw VerikDslException()
}

operator fun _uint.times(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _int.rem(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _int.rem(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _sint.rem(x: _int): _sint {
    throw VerikDslException()
}

operator fun _sint.rem(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _sint.rem(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _uint.rem(x: _int): _uint {
    throw VerikDslException()
}

operator fun _uint.rem(x: _sint): _uint {
    throw VerikDslException()
}

operator fun _uint.rem(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _int.div(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _int.div(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _sint.div(x: _int): _sint {
    throw VerikDslException()
}

operator fun _sint.div(x: _sint): _sint {
    throw VerikDslException()
}

operator fun _sint.div(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _uint.div(x: _int): _uint {
    throw VerikDslException()
}

operator fun _uint.div(x: _sint): _uint {
    throw VerikDslException()
}

operator fun _uint.div(x: _uint): _uint {
    throw VerikDslException()
}

operator fun _int.compareTo(x: _sint): Int {
    throw VerikDslException()
}

operator fun _int.compareTo(x: _uint): Int {
    throw VerikDslException()
}

operator fun _sint.compareTo(x: _int): Int {
    throw VerikDslException()
}

operator fun _sint.compareTo(x: _sint): Int {
    throw VerikDslException()
}

operator fun _uint.compareTo(x: _int): Int {
    throw VerikDslException()
}

operator fun _uint.compareTo(x: _uint): Int {
    throw VerikDslException()
}