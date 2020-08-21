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

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")

package verik.common.data

import verik.common.*

open class _sint internal constructor(
        val SIZE: _int,
        private val bits: BooleanArray
): _data {

    constructor(SIZE: _int): this(SIZE, BooleanArray(0))

    operator fun get(n: _int): _bool {
        throw VerikDslException()
    }

    operator fun get(n: _uint): _bool {
        throw VerikDslException()
    }

    operator fun get(range: IntRange): _uint {
        throw VerikDslException()
    }

    override fun toString() = "0x${getHexString(SIZE, bits)}"

    override fun equals(other: Any?): Boolean {
        return if (other is _sint) {
            other.SIZE == SIZE && other.bits.contentEquals(bits)
        } else false
    }

    override fun hashCode(): Int {
        return 31 * SIZE + bits.hashCode()
    }
}

class sint: _sint {

    constructor(SIZE: _int, value: _int): super(SIZE, getBits(SIZE, value))

    constructor(value: _int): super(0, getBits(0, value)) {
        throw VerikDslException()
    }
}

infix fun _sint.put(x: _sint) {
    throw VerikDslException()
}

infix fun _sint.put(x: _int) {
    throw VerikDslException()
}

infix fun _sint.reg(x: _sint) {
    throw VerikDslException()
}

infix fun _sint.reg(x: _int) {
    throw VerikDslException()
}

infix fun _sint.con(x: _sint) {
    throw VerikDslException()
}

infix fun _sint.con(x: _int) {
    throw VerikDslException()
}

infix fun _sint.eq(x: _sint): _bool {
    throw VerikDslException()
}

infix fun _int.eq(x: _sint): _bool {
    throw VerikDslException()
}

infix fun _sint.eq(x: _int): _bool {
    throw VerikDslException()
}

infix fun _sint.neq(x: _sint): _bool {
    throw VerikDslException()
}

infix fun _int.neq(x: _sint): _bool {
    throw VerikDslException()
}

infix fun _sint.neq(x: _int): _bool {
    throw VerikDslException()
}
