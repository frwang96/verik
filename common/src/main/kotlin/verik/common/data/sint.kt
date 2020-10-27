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

    override fun equals(other: _any?): Boolean {
        return if (other is _sint) {
            other.SIZE == SIZE && other.bits.contentEquals(bits)
        } else false
    }

    override fun hashCode(): Int {
        return 31 * SIZE + bits.hashCode()
    }
}

fun sint(SIZE: _int, value: _int): _sint {
    return _sint(SIZE, getBits(SIZE, value))
}

fun sint(value: _string): _sint {
    throw VerikDslException()
}

infix fun _sint.init(x: _sint) {
    throw VerikDslException()
}

infix fun _sint.type(x: _sint) {
    throw VerikDslException()
}
