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

@file:Suppress("unused")

package verik.data

import verik.base.*

/**
 * Enumerated type with encoded representation [value]. The following functions are automatically generated.
 *
 *      infix fun _enum.set(x: _enum): _unit
 */
interface _enum {

    val value: _ubit

    /**
     * (UNIMPLEMENTED) ???
     */
    fun is_unknown(): _bool {
        throw VerikDslException()
    }
}

/**
 * (UNIMPLEMENTED) ???
 */
fun enum_sequential(): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
fun enum_one_hot(): _ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) ???
 */
fun enum_zero_one_hot(): _ubit {
    throw VerikDslException()
}
