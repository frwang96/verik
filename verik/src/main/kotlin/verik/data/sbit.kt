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
        internal val value: _string
): _data {

    constructor(SIZE: _int): this(SIZE, "")

    fun init(value: _int): _sbit {
        throw VerikDslException()
    }

    operator fun get(n: _int): _bool {
        throw VerikDslException()
    }

    operator fun get(n: _ubit): _bool {
        throw VerikDslException()
    }

    operator fun get(range: IntRange): _ubit {
        throw VerikDslException()
    }
}

fun sbit(SIZE: _int, value: _int): _sbit {
    return _sbit(SIZE, value.toString())
}

fun sbit(value: _int): _sbit {
    throw VerikDslException()
}

fun sbit(value: _string): _sbit {
    throw VerikDslException()
}

infix fun _sbit.set(x: _sbit) {
    throw VerikDslException()
}
