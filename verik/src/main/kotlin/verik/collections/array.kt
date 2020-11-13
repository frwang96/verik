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

package verik.collections

import verik.base.*
import verik.data.*

open class _array<TYPE>(override val _TYPE: TYPE, val SIZE: _int): _data, _collection, _indexed<TYPE>(_TYPE)

fun <TYPE> array(_TYPE: TYPE, SIZE: _int): _array<TYPE> {
    throw VerikDslException()
}

fun <TYPE> array(_TYPE: TYPE, SIZE: _int, x: TYPE): _array<TYPE> {
    throw VerikDslException()
}

fun <TYPE> array(_TYPE: TYPE, vararg x: TYPE): _array<TYPE> {
    throw VerikDslException()
}

infix fun <TYPE> _array<TYPE>.init(x: _array<TYPE>) {
    throw VerikDslException()
}
