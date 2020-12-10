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

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "unused")

package verik.data

import verik.base.*

open class _ubit internal constructor(
        val SIZE: _int,
        private val bits: BooleanArray
): _data {

    constructor(SIZE: _int): this(SIZE, BooleanArray(0))

    fun unpack(x: _bool) = false

    fun <T: _data> unpack(x: T) = x

    operator fun get(n: _int): _bool {
        throw VerikDslException()
    }

    operator fun get(n: _ubit): _bool {
        throw VerikDslException()
    }

    operator fun get(range: IntRange): _ubit {
        throw VerikDslException()
    }

    override fun toString() = "0x${get_hex_string(SIZE, bits)}"

    override fun equals(other: _any?): Boolean {
        return if (other is _ubit) {
            other.SIZE == SIZE && other.bits.contentEquals(bits)
        } else false
    }

    override fun hashCode(): Int {
        return 31 * SIZE + bits.hashCode()
    }
}

fun ubit(SIZE: _int, value: _int): _ubit {
    return _ubit(SIZE, get_bits(SIZE, value))
}

fun ubit(value: _int): _ubit {
    throw VerikDslException()
}

infix fun _ubit.set(x: _ubit) {
    throw VerikDslException()
}
