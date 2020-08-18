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

open class _stack<TYPE>(override val _TYPE: TYPE): _collection, _indexed<TYPE>(_TYPE) {

    fun push(x: TYPE) {
        throw VerikDslException()
    }

    fun peek(x: TYPE): TYPE {
        throw VerikDslException()
    }

    fun pop(x: TYPE): TYPE {
        throw VerikDslException()
    }
}

class stack<TYPE>(_TYPE: TYPE): _stack<TYPE>(_TYPE) {

    init {
        throw VerikDslException()
    }
}

infix fun <TYPE> _stack<TYPE>.put(x: _stack<TYPE>) {
    throw VerikDslException()
}

infix fun <TYPE> _stack<TYPE>.eq(x: _stack<TYPE>): _bool {
    throw VerikDslException()
}

infix fun <TYPE> _stack<TYPE>.neq(x: _stack<TYPE>): _bool {
    throw VerikDslException()
}
