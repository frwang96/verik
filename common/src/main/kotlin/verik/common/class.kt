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

package verik.common

import verik.common.data.*

// fun class() = _class()
// operator fun _class.plus(x: _class): _class { throw Exception() }
// operator fun _class.times(x: _class): _class { throw Exception() }
// infix fun _class.init(x: _class) {}
// infix fun _class.eq(x: _class): _bool {}
// infix fun _class.neq(x: _class): _bool {}
interface _class: _instance

infix fun <T: _class> T.with(block: (T) -> _unit): T {
    throw VerikDslException()
}

fun apply(x: _class) {
    throw VerikDslException()
}

operator fun _class.plus(x: _null): _class {
    throw VerikDslException()
}

operator fun _class.times(x: _null): _class {
    throw VerikDslException()
}

infix fun _class.init(x: _null) {
    throw VerikDslException()
}

infix fun _class.eq(x: _null): _bool {
    throw VerikDslException()
}

infix fun _class.neq(x: _null): _bool {
    throw VerikDslException()
}
