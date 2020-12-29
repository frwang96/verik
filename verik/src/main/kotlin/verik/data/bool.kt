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

package verik.data

import verik.base.*

//////////////////////////////////////////////////////////////////////////////// BUILD
typealias _bool = Boolean

operator fun Boolean.Companion.invoke(): _bool {
    throw VerikDslException()
}

infix fun _bool.set(x: _bool) {
    throw VerikDslException()
}

fun _bool.pack(): _ubit {
    throw VerikDslException()
}

fun _bool.is_unknown(): _bool {
    throw VerikDslException()
}

fun _bool.is_x(): _bool {
    throw VerikDslException()
}

fun _bool.is_z(): _bool {
    throw VerikDslException()
}

fun _bool.to_x(): _bool {
    throw VerikDslException()
}

fun _bool.to_z(): _bool {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Represents a value that is either true or false. Aliases the Kotlin type
// * [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/).
// */
//class _bool: _logic()
////////////////////////////////////////////////////////////////////////////////
