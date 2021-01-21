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
 * (UNIMPLEMENTED) A stack of [TYPE]
 */
class Stack<Type>(val TYPE: Type): Collection() {

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun init(x: Stack<Type>) {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    /**
     * (UNIMPLEMENTED) Push [x] onto the stack.
     */
    fun push(x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Peek at the top of the stack.
     */
    fun peek(x: Type): Type {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Pop from the top of the stack.
     */
    fun pop(x: Type): Type {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Clears the contents of the stack.
     */
    fun clear() {
        throw VerikDslException()
    }
}
