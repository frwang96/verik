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

@file:Suppress("unused", "UNUSED_PARAMETER")

package verik.data
import verik.base.*

//////////////////////////////////////////////////////////////////////////////// BUILD
/**
 * (UNIMPLEMENTED) Pack the enum as a [Ubit].
 */
fun <E: Enum<E>> Enum<E>.pack(): Ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns true if the enum is unknown.
 */
fun <E: Enum<E>> Enum<E>.is_unknown(): Boolean {
    throw VerikDslException()
}

/**
 * Returns an enum set to X.
 */
fun <E: Enum<E>> x(type: E): E {
    throw VerikDslException()
}

/**
 * Returns an enum set to Z.
 */
fun <E: Enum<E>> z(type: E): E {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * The base class of all enumerated types. Corresponds to the Kotlin type
// * [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/) and SystemVerilog enums.
// *
// *      enum class Op { ADD, SUB }
// */
//abstract class Enum: Logic()
////////////////////////////////////////////////////////////////////////////////
