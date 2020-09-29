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

typealias _bool = Boolean

operator fun Boolean.Companion.invoke(): _bool {
    throw VerikDslException()
}

fun _bool.is_unknown(): Boolean {
    throw VerikDslException()
}

fun _bool.pack(): _uint {
    throw VerikDslException()
}

infix fun _bool.put(x: _bool) {
    throw VerikDslException()
}

infix fun _bool.put(x: _x) {
    throw VerikDslException()
}

infix fun _bool.put(x: _z) {
    throw VerikDslException()
}

infix fun _bool.reg(x: _bool) {
    throw VerikDslException()
}

infix fun _bool.reg(x: _x) {
    throw VerikDslException()
}

infix fun _bool.reg(x: _z) {
    throw VerikDslException()
}

infix fun _bool.con(x: _bool) {
    throw VerikDslException()
}

infix fun _bool.con(x: _x) {
    throw VerikDslException()
}

infix fun _bool.con(x: _z) {
    throw VerikDslException()
}

infix fun _bool.eq(x: _bool): _bool {
    throw VerikDslException()
}

infix fun _bool.eq(x: _x): _bool {
    throw VerikDslException()
}

infix fun _bool.eq(x: _z): _bool {
    throw VerikDslException()
}

infix fun _bool.neq(x: _bool): _bool {
    throw VerikDslException()
}

infix fun _bool.neq(x: _x): _bool {
    throw VerikDslException()
}

infix fun _bool.neq(x: _z): _bool {
    throw VerikDslException()
}
