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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.data

import verik.base.*

//////////////////////////////////////////////////////////////////////////////// BUILD
/**
 * Pack the int as a [Ubit].
 */
@Deprecated("UNIMPLEMENTED")
fun Int.pack(): Ubit {
    throw VerikException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Represents a 32-bit signed integer. Corresponds to the Kotlin type
// * [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/) and the SystemVerilog type int.
// */
//class Int: Data() {
//
//    /**
//     * Adds [x] to this value.
//     */
//    operator fun plus(x: Int): Int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Subtracts [x] from this value.
//     */
//    operator fun minus(x: Int): Int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Multiplies [x] to this value.
//     */
//    operator fun times(x: Int): Int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Takes the remainder when divided by [x].
//     */
//    operator fun rem(x: Int): Int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Takes the quotient when divided by [x].
//     */
//    operator fun div(x: Int): Int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Arithmetic left shift by [x] bits.
//     */
//    infix fun shl(x: Int): Int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Arithmetic right shift by [x] bits with sign extension.
//     */
//    infix fun shr(x: Int): Int {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////

/**
 * Returns the [Int] type.
 */
fun t_Int(): Int {
    throw VerikException()
}
