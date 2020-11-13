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

package verik.data

import verik.base.VerikDslException

fun inv(x: _sint): _uint {
    throw VerikDslException()
}

fun inv(x: _uint): _uint {
    throw VerikDslException()
}

fun red_and(x: _sint): _bool {
    throw VerikDslException()
}

fun red_and(x: _uint): _bool {
    throw VerikDslException()
}

fun red_or(x: _sint): _bool {
    throw VerikDslException()
}

fun red_or(x: _uint): _bool {
    throw VerikDslException()
}

fun red_nand(x: _sint): _bool {
    throw VerikDslException()
}

fun red_nand(x: _uint): _bool {
    throw VerikDslException()
}

fun red_nor(x: _sint): _bool {
    throw VerikDslException()
}

fun red_nor(x: _uint): _bool {
    throw VerikDslException()
}

fun red_xor(x: _sint): _bool {
    throw VerikDslException()
}

fun red_xor(x: _uint): _bool {
    throw VerikDslException()
}

fun red_xnor(x: _sint): _bool {
    throw VerikDslException()
}

fun red_xnor(x: _uint): _bool {
    throw VerikDslException()
}

infix fun _int.and(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _int.and(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.and(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.and(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _sint.and(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.and(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.and(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.and(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.or(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _int.or(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.or(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.or(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _sint.or(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.or(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.or(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.or(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.xor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _int.xor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _sint.xor(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.xor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _sint.xor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.xor(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.xor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.xor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.nand(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _int.nand(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _bool.nand(x: _bool): _bool {
    throw VerikDslException()
}

infix fun _sint.nand(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.nand(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _sint.nand(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.nand(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.nand(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.nand(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.nor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _int.nor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _bool.nor(x: _bool): _bool {
    throw VerikDslException()
}

infix fun _sint.nor(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.nor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _sint.nor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.nor(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.nor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.nor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _int.xnor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _int.xnor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _bool.xnor(x: _bool): _bool {
    throw VerikDslException()
}

infix fun _sint.xnor(x: _int): _uint {
    throw VerikDslException()
}

infix fun _sint.xnor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _sint.xnor(x: _uint): _uint {
    throw VerikDslException()
}

infix fun _uint.xnor(x: _int): _uint {
    throw VerikDslException()
}

infix fun _uint.xnor(x: _sint): _uint {
    throw VerikDslException()
}

infix fun _uint.xnor(x: _uint): _uint {
    throw VerikDslException()
}