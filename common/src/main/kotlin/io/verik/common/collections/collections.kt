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

import io.verik.common.*
import io.verik.common.data.*

class _group<_T: _component>(val LEN: Int, val T: _T): Iterable<_T> {

    operator fun get(n: Int) = T

    operator fun get(n: _uint) = T

    override fun iterator() = _iterator()

    inner class _iterator: Iterator<_T> {

        override fun hasNext() = false

        override fun next() = T
    }
}

infix fun <_T: _component> _group<_T>.with(block: (_group<_T>) -> Unit) = this

class _array<_T: _instance>(val LEN: Int, val T: _T): _class, _data, Iterable<_T> {

    operator fun get(n: Int) = T

    operator fun get(n: _uint) = T

    override fun iterator() = _iterator()

    inner class _iterator: Iterator<_T> {

        override fun hasNext() = false

        override fun next() = T
    }
}

fun <_T: _instance> array(x: _T, vararg y: _T) = _array(0, x)

fun <_T: _instance> array(LEN: Int, x: _T) = _array(0, x)

infix fun <_T: _instance> _array<_T>.for_each(block: (_T) -> Unit) {}

infix fun <_T: _instance> _array<_T>.put(x: _array<_T>?) {}

infix fun <_T: _instance> _array<_T>.reg(x: _array<_T>?) {}

infix fun <_T: _instance> _array<_T>.drive(x: _array<_T>?) {}

infix fun <_T: _instance> _array<_T>.con(x: _array<_T>?) {}
