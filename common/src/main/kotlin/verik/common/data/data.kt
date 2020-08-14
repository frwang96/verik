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

package verik.common.data

import verik.common.*

interface _data: _instance {

    fun is_unknown(): Boolean {
        throw VerikDslException("function")
    }

    fun is_x(): Boolean {
        throw VerikDslException("function")
    }

    fun is_z(): Boolean {
        throw VerikDslException("function")
    }

    fun pack(): _uint {
        throw VerikDslException("function")
    }
}

infix fun _data.put(x: _x) {
    throw VerikDslException("function")
}

infix fun _data.put(x: _z) {
    throw VerikDslException("function")
}

infix fun _data.reg(x: _x) {
    throw VerikDslException("function")
}

infix fun _data.reg(x: _z) {
    throw VerikDslException("function")
}

infix fun _data.con(x: _x) {
    throw VerikDslException("function")
}

infix fun _data.con(x: _z) {
    throw VerikDslException("function")
}
