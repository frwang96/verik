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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.collection

import verik.base.*

/**
 * (UNIMPLEMENTED) A stack of [_TYPE]
 */
class _stack<TYPE>(val _TYPE: TYPE): _indexed<TYPE>() {

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: _stack<TYPE>) {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    /**
     * (UNIMPLEMENTED) Push [x] onto the stack.
     */
    fun push(x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Peek at the top of the stack.
     */
    fun peek(x: TYPE): TYPE {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Pop from the top of the stack.
     */
    fun pop(x: TYPE): TYPE {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) Construct a stack of [_TYPE].
 */
fun <TYPE> stack(_TYPE: TYPE): _stack<TYPE> {
    throw VerikDslException()
}
