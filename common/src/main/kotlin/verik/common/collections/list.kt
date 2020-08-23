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

class _list<TYPE>(override val _TYPE: TYPE): _collection, _indexed<TYPE>(_TYPE)

fun <TYPE> list(_TYPE: TYPE, size: _int): _list<TYPE> {
    throw VerikDslException()
}

fun <TYPE> list(_TYPE: TYPE, size: _int, x: TYPE): _list<TYPE> {
    throw VerikDslException()
}

fun <TYPE> list(_TYPE: TYPE, vararg x: TYPE): _list<TYPE> {
    throw VerikDslException()
}

infix fun <TYPE> _list<TYPE>.put(x: _list<TYPE>) {
    throw VerikDslException()
}

infix fun <TYPE> _list<TYPE>.eq(x: _list<TYPE>): _bool {
    throw VerikDslException()
}

infix fun <TYPE> _list<TYPE>.neq(x: _list<TYPE>): _bool {
    throw VerikDslException()
}
