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

import verik.data.*

//////////////////////////////////////////////////////////////////////////////// BUILD
abstract class _component: _any() {

    fun to_string(): _string {
        throw VerikDslException()
    }
}

infix fun <TYPE: _component> TYPE.with(block: (TYPE) -> _unit): TYPE {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Component that can be synthesized to hardware.
// */
//abstract class _component: _any() {
//
//    /**
//     * Instantiate component with connections in [block].
//     */
//    infix fun with (block: (_component) -> _unit): _component {
//        throw VerikDslException()
//    }
//}
////////////////////////////////////////////////////////////////////////////////

