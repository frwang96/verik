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
 * Represents an sequence of bits that is unsigned. Corresponds to SystemVerilog packed logic.
 * @property WIDTH the width in bits
 */
class Ubit internal constructor(
    val WIDTH: Int,
    internal val value: String
): Logic() {

    /**
     * Returns the [Ubit] type.
     * @param WIDTH the width in bits
     */
    constructor(WIDTH: Int): this(WIDTH, "")

//////////////////////////////////////////////////////////////////////////////// BUILD
    /**
     * Pack the [Ubit] as a [Ubit].
     */
    @Deprecated("UNIMPLEMENTED")
    fun pack(): Ubit {
        throw VerikDslException()
    }

    /**
     * Returns true if any bit of the [Ubit] is unknown.
     */
    @Deprecated("UNIMPLEMENTED")
    fun is_unknown(): Boolean {
        throw VerikDslException()
    }
////////////////////////////////////////////////////////////////////////////////

    operator fun unaryPlus(): Ubit {
        throw VerikDslException()
    }

    operator fun unaryMinus(): Ubit {
        throw VerikDslException()
    }

    operator fun compareTo(x: Ubit): Int {
        throw VerikDslException()
    }

    /**
     * Unpack the [Ubit] to a particular type.
     * @param type the type to unpack to
     */
    @Deprecated("UNIMPLEMENTED")
    fun <T: Data> unpack(type: T): T {
        throw VerikDslException()
    }

    /**
     * Get bit [n].
     */
    operator fun get(n: Int): Boolean {
        throw VerikDslException()
    }

    /**
     * Get bit [n].
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun get(n: Ubit): Boolean {
        throw VerikDslException()
    }

    /**
     * Get range from bits [n] to [m].
     */
    operator fun get(n: Int, m: Int): Ubit {
        throw VerikDslException()
    }

    /**
     * Set bit [n].
     */
    operator fun set(n: Int, value: Boolean) {
        throw VerikDslException()
    }

    /**
     * Set bit [n].
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun set(n: Ubit, value: Boolean) {
        throw VerikDslException()
    }

    /**
     * Set range from bits [n] to [m].
     */
    operator fun set(n: Int, m: Int, value: Ubit) {
        throw VerikDslException()
    }

    /**
     * Increment by one.
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun inc(): Ubit {
        throw VerikDslException()
    }

    /**
     * Decrement by one.
     */
    @Deprecated("UNIMPLEMENTED")
    operator fun dec(): Ubit {
        throw VerikDslException()
    }

    /**
     * Returns true if zero.
     */
    operator fun not(): Boolean {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_ubit(n) + _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun plus(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with truncated width `_ubit(n) + _sbit(m) = _ubit(max(n, m))`.
     */
    operator fun plus(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_ubit(n) - _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with truncated width `_ubit(n) - _sbit(m) = _ubit(max(n, m))`.
     */
    operator fun minus(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_ubit(n) * _ubit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with truncated width `_ubit(n) * _sbit(m) = _ubit(max(n, m))`.
     */
    operator fun times(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width  `_ubit(n) + _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Add [x] to this value with full width `_ubit(n) + _sbit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun add(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_ubit(n) - _ubit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Subtract [x] from this value with full width `_ubit(n) - _sbit(m) = _ubit(max(n, m) + 1)`.
     */
    infix fun sub(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width `_ubit(n) * _ubit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Multiply [x] to this value with full width  `_ubit(n) * _sbit(m) = _ubit(n + m)`.
     */
    infix fun mul(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    infix fun shl(x: Int): Ubit {
        throw VerikDslException()
    }

    /**
     * Logical left shift by [x] bits.
     */
    @Deprecated("UNIMPLEMENTED")
    infix fun shl(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Logical right shift by [x] bits.
     */
    infix fun shr(x: Int): Ubit {
        throw VerikDslException()
    }

    /**
     * Logical right shift by [x] bits.
     */
    @Deprecated("UNIMPLEMENTED")
    infix fun shr(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise and.
     */
    infix fun and(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise or.
     */
    infix fun or(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Ubit): Ubit {
        throw VerikDslException()
    }

    /**
     * Takes the bitwise xor.
     */
    infix fun xor(x: Sbit): Ubit {
        throw VerikDslException()
    }

    /**
     * Inverts the bits.
     */
    fun inv(): Ubit {
        throw VerikDslException()
    }

    /**
     * Reduce bits with and.
     */
    fun red_and(): Boolean {
        throw VerikDslException()
    }

    /**
     * Reduce bits with or.
     */
    fun red_or(): Boolean {
        throw VerikDslException()
    }

    /**
     * Reduce bits with xor.
     */
    fun red_xor(): Boolean {
        throw VerikDslException()
    }

    /**
     * Extend to [width] with zero extension.
     */
    fun ext(width: Int): Ubit {
        throw VerikDslException()
    }

    /**
     * Truncate to [width].
     */
    fun tru(width: Int): Ubit {
        throw VerikDslException()
    }
}

/**
 * Returns the [Ubit] type.
 * @param WIDTH the width in bits
 */
fun t_Ubit(WIDTH: Int): Ubit {
    return Ubit(WIDTH)
}

/**
 * Constructs a [Ubit].
 * @param WIDTH the width in bits
 * @param value the value
 */
fun u(WIDTH: Int, value: Int): Ubit {
    return Ubit(WIDTH, value.toString())
}

/**
 * Constructs a [Ubit]. The width is automatically inferred from the context.
 * @param value the value
 */
fun u(value: Int): Ubit {
    throw VerikDslException()
}

/**
 * Constructs a [Ubit].
 * @param value the value
 */
@Deprecated("UNIMPLEMENTED")
fun u(value: String): Ubit {
    throw VerikDslException()
}

/**
 * Constructs a [Ubit].
 * @param value the value
 */
fun u(value: Sbit): Ubit {
    throw VerikDslException()
}

//////////////////////////////////////////////////////////////////////////////// BUILD
/**
 * Returns a [Ubit] with all bits set to X.
 * @param type the type of the [Ubit]
 */
fun x(type: Ubit): Ubit {
    throw VerikDslException()
}

/**
 * Returns a [Ubit] with all bits set to Z.
 * @param type the type of the [Ubit]
 */
fun z(type: Ubit): Ubit {
    throw VerikDslException()
}
////////////////////////////////////////////////////////////////////////////////
