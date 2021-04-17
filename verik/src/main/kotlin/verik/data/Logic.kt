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

@file:Suppress("unused")

package verik.data

import verik.base.*

/**
 * Represents data that has four-state values.
 */
//////////////////////////////////////////////////////////////////////////////// BUILD
typealias Logic = Data
//////////////////////////////////////////////////////////////////////////////// DOKKA
//abstract class Logic: Data() {
//
//    /**
//     * Returns true if any bit is unknown.
//     */
//    @Deprecated("UNIMPLEMENTED")
//    fun is_unknown(): Boolean {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////

/**
 * Returns a value with all bits set to X.
 * @param T the type of the value
 */
@Deprecated("UNIMPLEMENTED")
fun <T: Logic> x(): T {
    throw VerikDslException()
}

/**
 * Returns a value with all bits set to Z.
 * @param T the type of the value
 */
@Deprecated("UNIMPLEMENTED")
fun <T: Logic> z(): T {
    throw VerikDslException()
}

/**
 * Returns a do not care value.
 * @param T the type of the value
 */
fun <T: Logic> d(): T {
    throw VerikDslException()
}
