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

abstract class _indexed<TYPE> internal constructor(open val _TYPE: TYPE): Iterable<TYPE> {

    operator fun get(n: Int): TYPE {
        throw VerikDslException("function")
    }

    operator fun get(n: _uint): TYPE {
        throw VerikDslException("function")
    }

    operator fun get(n: _sint): TYPE {
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

infix fun <TYPE> _indexed<TYPE>.for_each(block: (TYPE) -> Unit) {
    throw VerikDslException("function")
}

infix fun <TYPE> _indexed<TYPE>.for_indexed(block: (Int, TYPE) -> Unit) {
    throw VerikDslException("function")
}
