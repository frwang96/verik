/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER", "unused", "MemberVisibilityCanBePrivate")

package verik.collection

import verik.base.*

/**
 * (UNIMPLEMENTED) A queue of [_TYPE].
 */
class _queue<TYPE>(val _TYPE: TYPE): _collection() {

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: _queue<TYPE>) {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    /**
     * (UNIMPLEMENTED) Add [x] to the queue.
     */
    fun add(x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Peek at the front of the queue.
     */
    fun peek(x: TYPE): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Poll from the front of the queue.
     */
    fun poll(x: TYPE): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Clears the contents of the queue.
     */
    fun clear() {
        throw VerikDslException()
    }
}
