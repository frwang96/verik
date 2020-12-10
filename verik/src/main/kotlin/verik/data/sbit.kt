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

open class _sbit internal constructor(
        val SIZE: _int,
        private val bits: BooleanArray
): _data {

    constructor(SIZE: _int): this(SIZE, BooleanArray(0))

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

    override fun equals(other: _any?): _bool {
        return if (other is _sbit) {
            other.SIZE == SIZE && other.bits.contentEquals(bits)
        } else false
    }

    override fun hashCode(): _int {
        return 31 * SIZE + bits.hashCode()
    }
}

fun sbit(SIZE: _int, value: _int): _sbit {
    return _sbit(SIZE, get_bits(SIZE, value))
}

fun sbit(value: _int): _sbit {
    throw VerikDslException()
}

infix fun _sbit.set(x: _sbit) {
    throw VerikDslException()
}
