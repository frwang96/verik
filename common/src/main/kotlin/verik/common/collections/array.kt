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

@file:Suppress("UNUSED_PARAMETER")

package verik.common.collections

import verik.common.*
import verik.common.data.*

open class _array<TYPE>(override val _TYPE: TYPE, val SIZE: _int): _data, _collection, _indexed<TYPE>(_TYPE)

class array<TYPE>: _array<TYPE> {

    constructor(_TYPE: TYPE, SIZE: _int): super(_TYPE, SIZE) {
        throw VerikDslException()
    }

    constructor(_TYPE: TYPE, SIZE: _int, x: TYPE): super(_TYPE, SIZE) {
        throw VerikDslException()
    }

    constructor(_TYPE: TYPE, vararg x: TYPE): super(_TYPE, 0) {
        throw VerikDslException()
    }
}

infix fun <TYPE> _array<TYPE>.put(x: _array<TYPE>) {
    throw VerikDslException()
}

infix fun <TYPE> _array<TYPE>.reg(x: _array<TYPE>) {
    throw VerikDslException()
}

infix fun <TYPE> _array<TYPE>.con(x: _array<TYPE>) {
    throw VerikDslException()
}

infix fun <TYPE> _array<TYPE>.eq(x: _array<TYPE>): _bool {
    throw VerikDslException()
}

infix fun <TYPE> _array<TYPE>.neq(x: _array<TYPE>): _bool {
    throw VerikDslException()
}