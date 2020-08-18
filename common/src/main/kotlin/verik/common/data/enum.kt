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

package verik.common.data

import verik.common.*

// infix fun _enum.put(x: _enum) {}
// infix fun _enum.reg(x: _enum) {}
// infix fun _enum.con(x: _enum) {}
// infix fun _enum.eq(x: _enum): _bool {}
// infix fun _enum.neq(x: _enum): _bool {}
interface _enum: _data {

    val value: _uint
}

fun enum_sequential(): _uint {
    throw VerikDslException()
}

fun enum_one_hot(): _uint {
    throw VerikDslException()
}

fun enum_zero_one_hot(): _uint {
    throw VerikDslException()
}
