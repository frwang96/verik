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

package io.verik.common.collections

import io.verik.common.*

open class _queue<TYPE: _instance>(override val _TYPE: TYPE): _collection, _indexed<TYPE>(_TYPE) {

    fun add(x: TYPE) {
        throw VerikDslException("function")
    }

    fun peek(x: TYPE): TYPE {
        throw VerikDslException("function")
    }

    fun poll(x: TYPE): TYPE {
        throw VerikDslException("function")
    }
}

class queue<TYPE: _instance>(_TYPE: TYPE): _queue<TYPE>(_TYPE) {

    init {
        throw VerikDslException("function")
    }
}

infix fun <TYPE: _instance> _queue<TYPE>.put(x: _queue<TYPE>) {
    throw VerikDslException("function")
}
