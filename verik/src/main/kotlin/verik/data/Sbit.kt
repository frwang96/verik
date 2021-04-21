/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "unused")

package verik.data

import verik.base.*

/**
 * Represents a sequence of bits that is signed. Corresponds to SystemVerilog packed signed logic.
 * @property WIDTH the width in bits
 */
class Sbit(val WIDTH: Int): Logic() {

//////////////////////////////////////////////////////////////////////////////// BUILD
    /**
     * Pack the [Sbit] as a [Ubit].
     */
    @Deprecated("UNIMPLEMENTED")
    fun pack(): Ubit {
        throw VerikException()
    }

    /**
     * Returns true if any bit of the [Sbit] is unknown.
     */
    @Deprecated("UNIMPLEMENTED")
    fun is_unknown(): Boolean {
        throw VerikException()
    }
////////////////////////////////////////////////////////////////////////////////

    operator fun unaryPlus(): Sbit {
        throw VerikException()
    }

    operator fun unaryMinus(): Sbit {
        throw VerikException()
    }

    operator fun compareTo(x: Sbit): Int {
        throw VerikException()
    }

    /**
     * Get bit [n].
     */
    operator fun get(n: Int): Boolean {
        throw VerikException()
    }

    /**
     * Get bit [n].
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Ubit): Boolean {
        throw VerikException()
    }

    /**
     * Get range from bits [n] to [m].
     */
    operator fun get(n: Int, m: Int): Ubit {
        throw VerikException()
    }

    /**
     * Set bit [n].
     */
    operator fun set(n: Int, value: Boolean) {
        throw VerikException()
    }

    /**
     * Set bit [n].
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun set(n: Ubit, value: Boolean) {
        throw VerikException()
    }

    /**
     * Set range from bits [n] to [m].
     */
    operator fun set(n: Int, m: Int, value: Ubit) {
        throw VerikException()
    }

    /**
     * Increment by one.
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun inc(): Sbit {
        throw VerikException()
    }

    /**
     * Decrement by one.
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun dec(): Sbit {
        throw VerikException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `_sbit(n) + _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun plus(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Add [x] to this value with truncated width `_sbit(n) + _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun plus(x: Sbit): Sbit {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `_sbit(n) - _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with truncated width `_sbit(n) - _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun minus(x: Sbit): Sbit {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `_sbit(n) * _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with truncated width `_sbit(n) * _sbit(m) = _sbit(max(n, m))`.
     */
    operator fun times(x: Sbit): Sbit {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `_sbit(n) + _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Add [x] to this value with full width `_sbit(n) + _sbit(m) = _sbit(max(n, m) + 1)`.
     */
    infix fun add(x: Sbit): Sbit {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `_sbit(n) - _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Subtract [x] from this value with full width `_sbit(n) - _sbit(m) = _sbit(max(n, m) + 1)`.
     */
    infix fun sub(x: Sbit): Sbit {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `_sbit(n) * _ubit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Multiply [x] to this value with full width `_sbit(n) * _sbit(m) = _sbit(n + m)`.
     */
    infix fun mul(x: Sbit): Sbit {
        throw VerikException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    infix fun shl(x: Int): Sbit {
        throw VerikException()
    }

    /**
     * Arithmetic left shift by [x] bits.
     */
    infix fun shl(x: Ubit): Sbit {
        throw VerikException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun shr(x: Int): Sbit {
        throw VerikException()
    }

    /**
     * Arithmetic right shift by [x] bits with sign extension.
     */
    infix fun shr(x: Ubit): Sbit {
        throw VerikException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Sbit): Ubit {
        throw VerikException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Sbit): Ubit {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Ubit): Ubit {
        throw VerikException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Sbit): Ubit {
        throw VerikException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit {
        throw VerikException()
    }

    /**
     * Reduce bits with and.
     */
    fun red_and(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with or.
     */
    fun red_or(): Boolean {
        throw VerikException()
    }

    /**
     * Reduce bits with xor.
     */
    fun red_xor(): Boolean {
        throw VerikException()
    }

    /**
     * Extend to [width] with sign extension.
     */
    fun ext(width: Int): Sbit {
        throw VerikException()
    }

    /**
     * Truncate to [width].
     */
    fun tru(width: Int): Sbit {
        throw VerikException()
    }
}

/**
 * Returns the [Sbit] type.
 * @param WIDTH the width in bits
 */
fun t_Sbit(WIDTH: Int): Sbit {
    throw VerikException()
}

/**
 * Constructs a [Sbit].
 * @param WIDTH the width in bits
 * @param value the value
 */
fun s(WIDTH: Int, value: Int): Sbit {
    throw VerikException()
}

/**
 * Constructs a [Sbit]. The width is automatically inferred from the context.
 * @param value the value
 */
fun s(value: Int): Sbit {
    throw VerikException()
}

/**
 * Constructs a [Sbit].
 * @param value the value
 */
@Deprecated("UNIMPLEMENTED")
fun s(value: String): Sbit {
    throw VerikException()
}

/**
 * Constructs a [Sbit].
 * @param value the value
 */
fun s(value: Ubit): Sbit {
    throw VerikException()
}

//////////////////////////////////////////////////////////////////////////////// BUILD
/**
 * Returns a [Sbit] with all bits set to X.
 * @param type the type of the [Sbit]
 */
fun x(type: Sbit): Sbit {
    throw VerikException()
}

/**
 * Returns a [Sbit] with all bits set to Z.
 * @param type the type of the [Sbit]
 */
fun z(type: Sbit): Sbit {
    throw VerikException()
}
////////////////////////////////////////////////////////////////////////////////
