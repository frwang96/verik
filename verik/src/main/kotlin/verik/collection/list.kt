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
import verik.data.*

/**
 * (UNIMPLEMENTED) A list of [_TYPE].
 */
class _list<TYPE>(val _TYPE: TYPE): _indexed<TYPE>() {

//////////////////////////////////////////////////////////////////////////////// BUILD
    infix fun set(x: _list<TYPE>) {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    /**
     * (UNIMPLEMENTED) Add [x] to the list.
     */
    fun add(x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Add [x] to the list at position [n].
     */
    fun insert(n: _int, x: TYPE) {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Remove the element at position [n].
     */
    fun remove(n: _int) {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) Constructs a list of type [_TYPE].
 */
fun <TYPE> list(_TYPE: TYPE): _list<TYPE> {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Constructs a list of type [_TYPE] with elements [x].
 */
fun <TYPE> list(_TYPE: TYPE, vararg x: TYPE): _list<TYPE> {
    throw VerikDslException()
}
