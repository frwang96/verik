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

open class _set<TYPE>(val _TYPE: TYPE): _collection, Iterable<TYPE> {

    fun add(x: TYPE) {
        throw VerikDslException()
    }

    fun contains(type: TYPE): _bool {
        throw VerikDslException()
    }

    fun remove(type: TYPE) {
        throw VerikDslException()
    }

    override fun iterator(): _iterator {
        throw VerikDslException()
    }

    inner class _iterator: Iterator<TYPE> {

        init {
            throw VerikDslException()
        }

        override fun hasNext(): _bool {
            throw VerikDslException()
        }

        override fun next(): TYPE {
            throw VerikDslException()
        }
    }
}

fun <TYPE> set(_TYPE: TYPE): _set<TYPE> {
    throw VerikDslException()
}

infix fun <TYPE> _set<TYPE>.init(x: _set<TYPE>) {
    throw VerikDslException()
}

infix fun <TYPE> _set<TYPE>.for_each(block: (TYPE) -> _unit) {
    throw VerikDslException()
}
