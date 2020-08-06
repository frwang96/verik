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

package io.verik.common.collections

import io.verik.common.data.*

open class _iterable<TYPE> internal constructor(open val _TYPE: TYPE): Iterable<TYPE> {

    operator fun get(n: Int) = _TYPE

    operator fun get(n: _uint) = _TYPE

    operator fun get(n: _sint) = _TYPE

    override fun iterator() = _iterator()

    inner class _iterator: Iterator<TYPE> {

        override fun hasNext() = false

        override fun next() = _TYPE
    }
}

infix fun <TYPE> _iterable<TYPE>.for_each(block: (TYPE) -> Unit) {}

infix fun <TYPE> _iterable<TYPE>.for_indexed(block: (Int, TYPE) -> Unit) {}
