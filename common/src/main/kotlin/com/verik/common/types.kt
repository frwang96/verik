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

package com.verik.common

typealias _bool = Boolean
operator fun Boolean.Companion.invoke() = false
fun _bool.is_unknown() = false
fun _bool.is_floating() = false
infix fun _bool.put(x: _bool?) {}
infix fun _bool.reg(x: _bool?) {}
infix fun _bool.drive(x: _bool?) {}
infix fun _bool.con(x: _bool?) {}
fun _bool.pack() = _uint(0)

interface _data: _instance
fun _data.is_unknown() = false
fun _data.is_floating() = false
infix fun <_T: _data> _T.con(x: _T?) {}
fun _data.pack() = _uint(0)

open class _sint internal constructor(): _data {
    constructor(LEN: Int): this()
    constructor(RANGE: IntRange): this()
    operator fun get(n: Int) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = _uint(0)
    operator fun get(range: _range) = _uint(0)
    val bin = ""
    val dec = ""
}
fun sint(LEN: Int, value: Int) = _sint(0)
fun sint(value: Int) = _sint(0)
infix fun _sint.put(x: Int) {}
infix fun _sint.reg(x: Int) {}
infix fun _sint.drive(x: Int?) {}
infix fun _sint.con(x: Int) {}
class _sint8: _sint(8)
class _sint16: _sint(16)
class _sint32: _sint(32)
class _sint64: _sint(64)

open class _uint internal constructor(): _data {
    constructor(LEN: Int): this()
    constructor(RANGE: IntRange): this()
    operator fun get(n: Int) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = _uint(0)
    operator fun get(range: _range) = _uint(0)
    val bin = ""
    val dec = ""
}
fun uint(LEN: Int, value: Int) = _uint(0)
fun uint(value: Int) = _uint(0)
infix fun _uint.put(x: Int) {}
infix fun _uint.reg(x: Int) {}
infix fun _uint.drive(x: Int?) {}
infix fun _uint.con(x: Int) {}
fun _uint.unpack(x: _bool) = false
fun <_T: _data> _uint.unpack(x: _T) = x
fun <_T: _instance> _uint.unpack(x: _array<_T>) = x
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

interface _struct: _data

class _range internal constructor(): Iterable<_uint> {
    override fun iterator() = _iterator()
    class _iterator: Iterator<_uint> {
        override fun hasNext() = false
        override fun next() = _uint(0)
    }
}
