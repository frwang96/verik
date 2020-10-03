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

open class _queue<TYPE>(override val _TYPE: TYPE): _collection, _indexed<TYPE>(_TYPE) {

    fun add(x: TYPE) {
        throw VerikDslException()
    }

    fun peek(x: TYPE): TYPE {
        throw VerikDslException()
    }

    fun poll(x: TYPE): TYPE {
        throw VerikDslException()
    }
}

fun <TYPE> queue(_TYPE: TYPE): _queue<TYPE> {
    throw VerikDslException()
}

operator fun <TYPE> _queue<TYPE>.plus(x: _queue<TYPE>): _queue<TYPE> {
    throw VerikDslException()
}

operator fun <TYPE> _queue<TYPE>.times(x: _queue<TYPE>): _queue<TYPE> {
    throw VerikDslException()
}

infix fun <TYPE> _queue<TYPE>.eq(x: _queue<TYPE>): _bool {
    throw VerikDslException()
}

infix fun <TYPE> _queue<TYPE>.neq(x: _queue<TYPE>): _bool {
    throw VerikDslException()
}
