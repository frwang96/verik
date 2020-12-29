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

package verik.base

import verik.data.*

//////////////////////////////////////////////////////////////////////////////// BUILD
typealias _unit = Unit

typealias _any = Any

fun _any.to_string(): _string {
    throw VerikDslException()
}

typealias _nothing = Nothing
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * (UNIMPLEMENTED) ???
// */
//abstract class _unit
//
///**
// * (UNIMPLEMENTED) ???
// */
//abstract class _any {
//
//    /**
//     * (UNIMPLEMENTED) ???
//     */
//    open fun to_string(): _string {
//        throw VerikDslException()
//    }
//}
//
///**
// * (UNIMPLEMENTED) ???
// */
//abstract class _nothing
////////////////////////////////////////////////////////////////////////////////

/**
 * (UNIMPLEMENTED) Explicitly label the type of a function declaration.
 */
fun type(x: _any, vararg y: _any) {
    throw VerikDslException()
}
