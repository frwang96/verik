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

package verik.base

//////////////////////////////////////////////////////////////////////////////// BUILD
/**
 * Returns a string representation of the object.
 */
@Deprecated("UNIMPLEMENTED")
fun Any.to_string(): String {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * The root of the type hierarchy. Corresponds to the Kotlin type
// * [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/).
// */
//abstract class Any {
//
//    /**
//     * Returns a string representation of the object.
//     */
//    @Deprecated("UNIMPLEMENTED")
//    open fun to_string(): String {
//        throw VerikDslException()
//    }
//}
//
///**
// * Nothing cannot be instantiated. Corresponds to the Kotlin type
// * [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html).
// */
//abstract class Nothing
//
///**
// * The type with only one value, the [Unit] object. Corresponds to the Kotlin type
// * [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/).
// */
//abstract class Unit: Any()
////////////////////////////////////////////////////////////////////////////////

/**
 * Explicitly label the type of a parameterized function parameter.
 * @param x the function parameter to label
 * @param type the type of [x]
 */
fun type(x: Any, type: Any) {
    throw VerikDslException()
}

/**
 * Explicitly label the type of a parameterized function return value.
 * @param type the type of the function return value
 */
fun type(type: Any) {
    throw VerikDslException()
}

/**
 * Get type expression from type parameter
 */
fun <T> t(): T {
    throw VerikDslException()
}