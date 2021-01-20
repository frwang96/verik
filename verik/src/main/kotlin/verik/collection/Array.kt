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
import verik.data.*

/**
 * (UNIMPLEMENTED) An array of [SIZE] and [TYPE].
 */
class Array<Type>(val SIZE: Int, val TYPE: Type): Collection(), Iterable<Type> {

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: Array<Type>) {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    /**
     * (UNIMPLEMENTED) Get element [n] of the array.
     */
    operator fun get(n: Int): Type {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Get element [n] of the array.
     */
    operator fun get(n: Ubit): Type {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set element [n] of the array.
     */
    operator fun set(n: Int, x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Set element [n] of the array.
     */
    operator fun set(n: Ubit, x: Type) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Iterate over the elements of the array.
     */
    infix fun for_each(block: (Type) -> Unit) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Clears the contents of the array.
     */
    fun clear() {
        throw VerikDslException()
    }

    override fun iterator(): Iterator<Type> {
        throw VerikDslException()
    }
}

@Suppress("FunctionName")
fun <Type> t_Array(SIZE: Int, TYPE: Type): Array<Type> {
    throw VerikDslException()
}
