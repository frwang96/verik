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

package io.verik.common.data

open class _uint (val LEN: Int): _data {

    val bin = ""
    val dec = ""

    fun unpack(x: _bool) = false

    fun <_T: _data> unpack(x: _T) = x

    operator fun get(n: Int) = false

    operator fun get(n: _uint) = false

    operator fun get(range: IntRange) = _uint(0)
}

fun uint(LEN: Int, value: Int) = _uint(0)

fun uint(value: Int) = _uint(0)

infix fun _uint.put(x: _uint?) {}

infix fun _uint.put(x: Int) {}

infix fun _uint.reg(x: _uint?) {}

infix fun _uint.reg(x: Int) {}

infix fun _uint.drive(x: _uint?) {}

infix fun _uint.drive(x: Int?) {}

infix fun _uint.con(x: _uint?) {}

infix fun _uint.con(x: Int) {}

class _uint8: _uint(8)

class _uint16: _uint(16)

class _uint32: _uint(32)

class _uint64: _uint(64)
