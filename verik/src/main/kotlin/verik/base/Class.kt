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
abstract class Class: Instance() {

    open fun to_string(): String {
        throw VerikDslException()
    }

    fun is_null(): Boolean {
        throw VerikDslException()
    }
}

fun <Type: Class> Type.to_null(): Type {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Base type for all user defined classes. They correspond to SystemVerilog classes.
// *
// *      class C: Class() {
// *
// *          fun init() {
// *              ...
// *          }
// *      }
// */
//abstract class Class: Instance() {
//
//    /**
//     * (UNIMPLEMENTED) Returns true if null.
//     */
//    fun is_null(): Boolean {
//        throw VerikDslException()
//    }
//
//    /**
//     * (UNIMPLEMENTED) Returns a null object from the type.
//     */
//    fun to_null(): Class {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////
