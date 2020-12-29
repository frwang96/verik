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

package verik.data

import verik.base.*

//////////////////////////////////////////////////////////////////////////////// BUILD
typealias _int = Int

operator fun Int.Companion.invoke(): _int {
    throw VerikDslException()
}

infix fun _int.set(x: _int) {
    throw VerikDslException()
}

fun _int.pack(): _ubit {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Represents a 32-bit signed integer. Aliases the Kotlin type
// * [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/).
// */
//class _int: _data() {
//
//    /**
//     * Adds [x] to this value.
//     */
//    operator fun plus(x: _int): _int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Subtracts [x] from this value.
//     */
//    operator fun minus(x: _int): _int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Multiplies [x] to this value.
//     */
//    operator fun times(x: _int): _int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Takes the remainder when divided by [x].
//     */
//    operator fun rem(x: _int): _int {
//        throw VerikDslException()
//    }
//
//    /**
//     * Takes the quotient when divided by [x].
//     */
//    operator fun div(x: _int): _int {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////
