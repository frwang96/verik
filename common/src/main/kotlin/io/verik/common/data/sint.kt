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

import io.verik.common.*
import java.util.*

open class _sint internal constructor(val SIZE: Int, internal val bits: BitSet): _data {

    constructor(SIZE: Int): this(SIZE, BitSet(0))

    val bin = ""
    val dec = ""

    operator fun get(n: Int) = false

    operator fun get(n: _uint) = false

    operator fun get(range: IntRange) = _uint(0)

    override fun toString() = "0x${getHexString(SIZE, bits)}"

    override fun equals(other: Any?): Boolean {
        return if (other is _sint) {
            other.SIZE == SIZE && other.bits == bits
        } else false
    }

    override fun hashCode(): Int {
        return 31 * SIZE + bits.hashCode()
    }
}

fun sint(SIZE: Int, value: Int): _sint {
    return _sint(SIZE, getBits(SIZE, value))
}

fun sint(value: Int): _sint {
    throw VerikDslException("sint(value: Int)")
}

infix fun _sint.put(x: _sint?) {}

infix fun _sint.put(x: Int) {}

infix fun _sint.reg(x: _sint?) {}

infix fun _sint.reg(x: Int) {}

infix fun _sint.drive(x: _sint?) {}

infix fun _sint.drive(x: Int?) {}

infix fun _sint.con(x: _sint?) {}

infix fun _sint.con(x: Int) {}

class _byte: _sint(8)

class _short: _sint(16)

class _int: _sint(32)

class _long: _sint(64)
