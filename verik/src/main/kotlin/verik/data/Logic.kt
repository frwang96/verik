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

//////////////////////////////////////////////////////////////////////////////// BUILD
typealias Logic = Data
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Represents a four-state logic value.
// */
//abstract class Logic: Data() {
//
//    /**
//     * (UNIMPLEMENTED) Returns true if any bit is unknown.
//     */
//    fun is_unknown(): Boolean {
//        throw VerikDslException()
//    }
//
//    /**
//     * (UNIMPLEMENTED) Returns true if any bit is X.
//     */
//    fun is_x(): Boolean {
//        throw VerikDslException()
//    }
//
//    /**
//     * (UNIMPLEMENTED) Returns true if any bit is Z.
//     */
//    fun is_z(): Boolean {
//        throw VerikDslException()
//    }
//
//    /**
//     * (UNIMPLEMENTED) Returns a value with all bits set to X from the type.
//     */
//    fun to_x(): Logic {
//        throw VerikDslException()
//    }
//
//    /**
//     * (UNIMPLEMENTED) Returns a value with all bits set to Z from the type.
//     */
//    fun to_z(): Logic {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////