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
    internal val value: _string
): _data {

    constructor(SIZE: _int): this(SIZE, "")

    fun unpack(x: _bool) = false

    fun unpack(x: _int) = 0

    fun <T: _data> unpack(x: T) = x

    operator fun get(n: _int): _bool {
        throw VerikDslException()
    }

    operator fun get(n: _ubit): _bool {
        throw VerikDslException()
    }

    operator fun get(range: _int_range): _ubit {
        throw VerikDslException()
    }

    operator fun set(n: _int, value: _bool) {
        throw VerikDslException()
    }

    operator fun set(n: _ubit, value: _bool) {
        throw VerikDslException()
    }

    operator fun set(range: _int_range, value: _ubit) {
        throw VerikDslException()
    }
}

fun ubit(SIZE: _int, value: _int): _ubit {
    return _ubit(SIZE, value.toString())
}

fun ubit(value: _int): _ubit {
    throw VerikDslException()
}

fun ubit(value: _string): _ubit {
    throw VerikDslException()
}

fun ubit(value: _sbit): _ubit {
    throw VerikDslException()
}

infix fun _ubit.set(x: _ubit) {
    throw VerikDslException()
}
