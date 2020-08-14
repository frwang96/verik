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

package verik.common.collections

import verik.common.*
import verik.common.data.*

open class _list<TYPE: _instance>(override val _TYPE: TYPE): _collection, _indexed<TYPE>(_TYPE)

class list<TYPE: _instance>: _list<TYPE> {

    constructor(_TYPE: TYPE, size: Int): super(_TYPE) {
        throw VerikDslException("function")
    }

    constructor(_TYPE: TYPE, size: Int, x: TYPE): super(_TYPE) {
        throw VerikDslException("function")
    }

    constructor(_TYPE: TYPE, vararg x: TYPE): super(_TYPE) {
        throw VerikDslException("function")
    }
}

infix fun <TYPE: _instance> _list<TYPE>.put(x: _list<TYPE>) {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _list<TYPE>.eq(x: _list<TYPE>): _bool {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _list<TYPE>.neq(x: _list<TYPE>): _bool {
    throw VerikDslException("function")
}
