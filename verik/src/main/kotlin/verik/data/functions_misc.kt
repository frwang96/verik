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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.data

import verik.base.*

fun cat(x: _any, vararg y: _any): _ubit {
    throw VerikDslException()
}

fun rep(n: _int, x: _bool): _ubit {
    throw VerikDslException()
}

fun rep(n: _int, x: _sbit): _ubit {
    throw VerikDslException()
}

fun rep(n: _int, x: _ubit): _ubit {
    throw VerikDslException()
}

fun max(x: _sbit, y: _sbit): _sbit {
    throw VerikDslException()
}

fun max(x: _ubit, y: _ubit): _ubit {
    throw VerikDslException()
}

fun min(x: _sbit, y: _sbit): _sbit {
    throw VerikDslException()
}

fun min(x: _ubit, y: _ubit): _ubit {
    throw VerikDslException()
}

fun signed(x: _ubit): _sbit {
    throw VerikDslException()
}

fun unsigned(x: _sbit): _ubit {
    throw VerikDslException()
}

fun ext(len: _int, x: _sbit): _sbit {
    throw VerikDslException()
}

fun ext(len: _int, x: _ubit): _ubit {
    throw VerikDslException()
}

fun tru(len: _int, x: _sbit): _sbit {
    throw VerikDslException()
}

fun tru(len: _int, x: _ubit): _ubit {
    throw VerikDslException()
}