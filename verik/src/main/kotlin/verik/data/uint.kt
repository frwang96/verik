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

package verik.data

import verik.base.VerikDslException
import verik.base._any

open class _uint internal constructor(
        val SIZE: _int,
        private val bits: BooleanArray
): _data {

    constructor(SIZE: _int): this(SIZE, BooleanArray(0))

    fun unpack(x: _bool) = false

    fun <T: _data> unpack(x: T) = x

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

    override fun equals(other: _any?): Boolean {
        return if (other is _uint) {
            other.SIZE == SIZE && other.bits.contentEquals(bits)
        } else false
    }

    override fun hashCode(): Int {
        return 31 * SIZE + bits.hashCode()
    }
}

fun uint(SIZE: _int, value: _int): _uint {
    return _uint(SIZE, getBits(SIZE, value))
}

fun uint(value: _int): _uint {
    throw VerikDslException()
}

fun uint(value: _string): _uint {
    throw VerikDslException()
}

infix fun _uint.init(x: _uint) {
    throw VerikDslException()
}

infix fun _uint.type(x: _uint) {
    throw VerikDslException()
}
