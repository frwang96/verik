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

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.common.types

import io.verik.common.*

typealias _bool = Boolean
operator fun Boolean.Companion.invoke() = false
fun _bool.is_unknown() = false
fun _bool.is_floating() = false
fun _bool.pack() = _uint(0)
infix fun _bool.put(x: _bool?) {}
infix fun _bool.reg(x: _bool?) {}
infix fun _bool.drive(x: _bool?) {}
infix fun _bool.con(x: _bool?) {}

interface _data: _instance
fun _data.is_unknown() = false
fun _data.is_floating() = false
fun _data.pack() = _uint(0)

open class _sint (val LEN: Int): _data {
    operator fun get(n: Int) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = _uint(0)
    val bin = ""
    val dec = ""
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

open class _uint (val LEN: Int): _data {
    operator fun get(n: Int) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = _uint(0)
    val bin = ""
    val dec = ""
}
fun uint(LEN: Int, value: Int) = _uint(0)
fun uint(value: Int) = _uint(0)
fun _uint.unpack(x: _bool) = false
fun <_T: _data> _uint.unpack(x: _T) = x
fun <_T: _instance> _uint.unpack(x: _array<_T>) = x
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

interface _enum: _data {
    companion object {
        val SEQUENTIAL = _uint(0)
        val ONE_HOT = _uint(0)
        val ZERO_ONE_HOT = _uint(0)
    }
}
// infix fun _enum.put(x: _enum?) {}
// infix fun _enum.reg(x: _enum?) {}
// infix fun _enum.drive(x: _enum?) {}
// infix fun _enum.con(x: _enum?) {}

interface _struct: _data
// infix fun _struct.put(x: _struct?) {}
// infix fun _struct.reg(x: _struct?) {}
// infix fun _struct.drive(x: _struct?) {}
// infix fun _struct.con(x: _struct?) {}

class _range internal constructor(): Iterable<_uint> {
    override fun iterator() = _iterator()
    class _iterator: Iterator<_uint> {
        override fun hasNext() = false
        override fun next() = _uint(0)
    }
}
