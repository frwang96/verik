/*
 * Copyright (c) 2021 Francis Wang
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

/**
 * Returns a random integer.
 */
fun random(): Int {
    throw VerikException()
}

/**
 * Returns a random integer from 0 to [max] exclusive.
 */
fun random(max: Int): Int {
    throw VerikException()
}

/**
 * Returns a random integer from [min] to [max] exclusive.
 */
@Deprecated("UNIMPLEMENTED")
fun random(min: Int, max: Int): Int {
    throw VerikException()
}

/**
 * Returns a random enum.
 * @param type the type of the enum
 */
//////////////////////////////////////////////////////////////////////////////// BUILD
fun <T: Enum<T>> random_enum(type: T): T {
    throw VerikException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
//fun <T: Enum> random_enum(type: T): T {
//    throw VerikDslException()
//}
////////////////////////////////////////////////////////////////////////////////
