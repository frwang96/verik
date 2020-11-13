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

package verik.common.base

import verik.common.data.*

typealias _unit = Unit
typealias _any = Any

fun <T: _data> X(x: T): T {
    throw VerikDslException()
}

fun X(x: _bool): _bool {
    throw VerikDslException()
}

fun X(x: _int): _int {
    throw VerikDslException()
}

fun <T: _data> Z(x: T): T {
    throw VerikDslException()
}

fun Z(x: _bool): _bool {
    throw VerikDslException()
}

fun Z(x: _int): _int {
    throw VerikDslException()
}

fun <T: _class> NULL(x: T): T {
    throw VerikDslException()
}

sealed class _return

object RETURN: _return() {

    init {
        throw VerikDslException()
    }
}

infix fun _return.type(x: _any) {
    throw VerikDslException()
}
