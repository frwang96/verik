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

// infix fun _enum.init(x: _enum) {}
interface _enum: _data {

    val value: _int

    fun encoding(): _uint {
        throw VerikDslException()
    }
}

fun enum_sequential(): _int {
    throw VerikDslException()
}

fun enum_one_hot(): _int {
    throw VerikDslException()
}

fun enum_zero_one_hot(): _int {
    throw VerikDslException()
}
