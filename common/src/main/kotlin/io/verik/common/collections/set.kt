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
import io.verik.common.data.*

open class _set<TYPE: _instance>(val _TYPE: TYPE): _collection, Iterable<TYPE> {

    fun add(x: TYPE) {
        throw VerikDslException("function")
    }

    fun contains(type: TYPE): _bool {
        throw VerikDslException("function")
    }

    fun remove(type: TYPE) {
        throw VerikDslException("function")
    }

    override fun iterator(): _iterator {
        throw VerikDslException("function")
    }

    inner class _iterator: Iterator<TYPE> {

        init {
            throw VerikDslException("_iterator")
        }

        override fun hasNext(): _bool {
            throw VerikDslException("function")
        }

        override fun next(): TYPE {
            throw VerikDslException("function")
        }
    }
}

class set<TYPE: _instance>(_TYPE: TYPE): _set<TYPE>(_TYPE) {

    init {
        throw VerikDslException("function")
    }
}

infix fun <TYPE: _instance> _set<TYPE>.for_each(block: (TYPE) -> Unit) {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _set<TYPE>.put(x: _set<TYPE>) {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _set<TYPE>.eq(x: _set<TYPE>): _bool {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _set<TYPE>.neq(x: _set<TYPE>): _bool {
    throw VerikDslException("function")
}
