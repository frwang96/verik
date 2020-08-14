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

open class _vector<TYPE: _instance>(override val _TYPE: TYPE): _collection, _indexed<TYPE>(_TYPE) {

    fun add(x: TYPE) {
        throw VerikDslException("function")
    }

    fun insert(index: Int, x: TYPE) {
        throw VerikDslException("function")
    }

    fun remove(index: Int) {
        throw VerikDslException("function")
    }
}

class vector<TYPE: _instance>(_TYPE: TYPE): _vector<TYPE>(_TYPE) {

    init {
        throw VerikDslException("function")
    }
}

infix fun <TYPE: _instance> _vector<TYPE>.put(x: _vector<TYPE>) {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _vector<TYPE>.eq(x: _vector<TYPE>): _bool {
    throw VerikDslException("function")
}

infix fun <TYPE: _instance> _vector<TYPE>.neq(x: _vector<TYPE>): _bool {
    throw VerikDslException("function")
}
