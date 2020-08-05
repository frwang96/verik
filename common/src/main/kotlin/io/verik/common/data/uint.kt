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

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")

package io.verik.common.data

import java.util.*

open class _uint internal constructor(val LEN: Int, internal val bits: BitSet): _data {

    constructor(LEN: Int): this(LEN, BitSet(0))

    val bin = ""
    val dec = ""

    fun unpack(x: _bool) = false

    fun <_T: _data> unpack(x: _T) = x

    operator fun get(n: Int) = false

    operator fun get(n: _uint) = false

    operator fun get(range: IntRange) = _uint(0)

    override fun toString() = "0x${getHexString(LEN, bits)}"

    override fun equals(other: Any?): Boolean {
        return if (other is _uint) {
            other.LEN == LEN && other.bits == bits
        } else false
    }

    override fun hashCode(): Int {
        return 31 * LEN + bits.hashCode()
    }
}

fun uint(LEN: Int, value: Int): _uint {
    return _uint(LEN, getBits(LEN, value))
}

fun uint(value: Int): _uint {
    return uint(32, value)
}

infix fun _uint.put(x: _uint?) {}

infix fun _uint.put(x: Int) {}

infix fun _uint.reg(x: _uint?) {}

infix fun _uint.reg(x: Int) {}

infix fun _uint.drive(x: _uint?) {}

infix fun _uint.drive(x: Int?) {}

infix fun _uint.con(x: _uint?) {}

infix fun _uint.con(x: Int) {}
