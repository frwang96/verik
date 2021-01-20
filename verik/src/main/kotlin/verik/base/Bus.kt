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
abstract class Bus: Component()
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * (UNIMPLEMENTED) Bus that carries signals between [modules][Module].
// */
//abstract class Bus: Component() {
//
//    /**
//     * (UNIMPLEMENTED) Used within [with][Component.with] expressions to connect busses.
//     */
//    infix fun con(x: Bus) {
//        throw VerikDslException()
//    }
//
//    /**
//     * (UNIMPLEMENTED) Used within constructor to assign immutable busses.
//     */
//    infix fun set(x: Bus) {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////