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

@file:Suppress("unused")

package verik.data
import verik.base.*

//////////////////////////////////////////////////////////////////////////////// BUILD
typealias _enum<E> = Enum<E>

fun <E: _enum<E>> _enum<E>.pack(): _ubit {
    throw VerikDslException()
}

fun <E: _enum<E>> _enum<E>.is_unknown(): _bool {
    throw VerikDslException()
}

fun <E: _enum<E>> _enum<E>.is_x(): _bool {
    throw VerikDslException()
}

fun <E: _enum<E>> _enum<E>.is_z(): _bool {
    throw VerikDslException()
}

fun <E: _enum<E>> _enum<E>.to_x(): E {
    throw VerikDslException()
}

fun <E: _enum<E>> _enum<E>.to_z(): E {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * The base class of all enumerated types. Aliases the Kotlin type
// * [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/)
// */
//abstract class _enum: _logic()
////////////////////////////////////////////////////////////////////////////////

/**
 * Label the enum values with sequential encoding.
 */
fun enum_sequential(): _ubit {
    throw VerikDslException()
}

/**
 * Label the enum values with one-hot encoding.
 */
fun enum_one_hot(): _ubit {
    throw VerikDslException()
}

/**
 * Label the enum values with zero-one-hot encoding.
 */
fun enum_zero_one_hot(): _ubit {
    throw VerikDslException()
}
