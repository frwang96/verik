/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.data

import verik.base.*
import verik.collection.*

/**
 * (UNIMPLEMENTED) Returns an array of [_int] from 0 to [n].
 */
fun range(n: _int): _array<_int> {
    throw VerikDslException()
}

/**
 * Returns the concatenation of the arguments.
 */
fun cat(x: _data, vararg y: _data): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns [x] replicated [n] times.
 */
fun rep(n: _int, x: _data): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the maximum of its arguments.
 */
fun max(x: _int, vararg y: _int): _int {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the maximum of its arguments.
 */
fun max(x: _sbit, vararg y: _sbit): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the maximum of its arguments.
 */
fun max(x: _ubit, vararg y: _ubit): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the minimum of its arguments.
 */
fun min(x: _int, vararg y: _int): _int {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the minimum of its arguments.
 */
fun min(x: _sbit, vararg y: _sbit): _sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the minimum of its arguments.
 */
fun min(x: _ubit, vararg y: _ubit): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the ceiling log base two of [x].
 */
fun log(x: _int): _int {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the exponential base two of [x].
 */
fun exp(x: _int): _int {
    throw VerikDslException()
}