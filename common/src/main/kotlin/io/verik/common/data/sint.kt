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

package io.verik.common.data

import io.verik.common.*
import java.util.*

open class _sint internal constructor(val SIZE: Int, internal val bits: BitSet): _data {

    constructor(SIZE: Int): this(SIZE, BitSet(0))

    operator fun get(n: Int): _bool {
        throw VerikDslException("function")
    }

    operator fun get(n: _uint): _bool {
        throw VerikDslException("function")
    }

    operator fun get(range: IntRange): _uint {
        throw VerikDslException("function")
    }

    override fun toString() = "0x${getHexString(SIZE, bits)}"

    override fun equals(other: Any?): Boolean {
        return if (other is _sint) {
            other.SIZE == SIZE && other.bits == bits
        } else false
    }

    override fun hashCode(): Int {
        return 31 * SIZE + bits.hashCode()
    }
}

class sint: _sint {

    constructor(SIZE: Int, value: Int): super(SIZE, getBits(SIZE, value))

    constructor(value: Int): super(0, getBits(0, value)) {
        throw VerikDslException("sint(value: Int)")
    }
}

infix fun _sint.put(x: _sint) {
    throw VerikDslException("function")
}

infix fun _sint.put(x: Int) {
    throw VerikDslException("function")
}

infix fun _sint.reg(x: _sint) {
    throw VerikDslException("function")
}

infix fun _sint.reg(x: Int) {
    throw VerikDslException("function")
}

infix fun _sint.con(x: _sint) {
    throw VerikDslException("function")
}

infix fun _sint.con(x: Int) {
    throw VerikDslException("function")
}

infix fun _sint.eq(x: _sint): _bool {
    throw VerikDslException("function")
}

infix fun Int.eq(x: _sint): _bool {
    throw VerikDslException("function")
}

infix fun _sint.eq(x: Int): _bool {
    throw VerikDslException("function")
}

infix fun _sint.neq(x: _sint): _bool {
    throw VerikDslException("function")
}

infix fun Int.neq(x: _sint): _bool {
    throw VerikDslException("function")
}

infix fun _sint.neq(x: Int): _bool {
    throw VerikDslException("function")
}

