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

open class _sint (val LEN: Int): _data {

    val bin = ""
    val dec = ""

    operator fun get(n: Int) = false

    operator fun get(n: _uint) = false

    operator fun get(range: IntRange) = _uint(0)
}

fun sint(LEN: Int, value: Int) = _sint(0)

fun sint(value: Int) = _sint(0)

infix fun _sint.put(x: _sint?) {}

infix fun _sint.put(x: Int) {}

infix fun _sint.reg(x: _sint?) {}

infix fun _sint.reg(x: Int) {}

infix fun _sint.drive(x: _sint?) {}

infix fun _sint.drive(x: Int?) {}

infix fun _sint.con(x: _sint?) {}

infix fun _sint.con(x: Int) {}

class _sint8: _sint(8)

class _sint16: _sint(16)

class _sint32: _sint(32)

class _sint64: _sint(64)
