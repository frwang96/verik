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

open class _map<KEY: _instance, VALUE: _instance>(val _KEY: KEY, val _VALUE: VALUE): _collection {

    operator fun get(key: KEY): VALUE {
        throw VerikDslException("function")
    }

    fun contains(key: KEY): _bool {
        throw VerikDslException("function")
    }

    fun remove(key: KEY) {
        throw VerikDslException("function")
    }
}

class map<KEY: _instance, VALUE: _instance>(_KEY: KEY, _VALUE: VALUE): _map<KEY, VALUE>(_KEY, _VALUE) {

    init {
        throw VerikDslException("function")
    }
}

infix fun <KEY: _instance, VALUE: _instance> _map<KEY, VALUE>.for_keys(block: (KEY) -> Unit) {
    throw VerikDslException("function")
}

infix fun <KEY: _instance, VALUE: _instance> _map<KEY, VALUE>.for_values(block: (VALUE) -> Unit) {
    throw VerikDslException("function")
}

infix fun <KEY: _instance, VALUE: _instance> _map<KEY, VALUE>.for_each(block: (KEY, VALUE) -> Unit) {
    throw VerikDslException("function")
}

infix fun <KEY: _instance, VALUE: _instance> _map<KEY, VALUE>.put(x: _map<KEY, VALUE>) {
    throw VerikDslException("function")
}

infix fun <KEY: _instance, VALUE: _instance> _map<KEY, VALUE>.eq(x: _map<KEY, VALUE>): _bool {
    throw VerikDslException("function")
}

infix fun <KEY: _instance, VALUE: _instance> _map<KEY, VALUE>.neq(x: _map<KEY, VALUE>): _bool {
    throw VerikDslException("function")
}
