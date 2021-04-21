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

package verik.data

import verik.base.*

//////////////////////////////////////////////////////////////////////////////// BUILD
/**
 * Pack the boolean as a [Ubit].
 */
@Deprecated("UNIMPLEMENTED")
fun Boolean.pack(): Ubit {
    throw VerikException()
}

/**
 * Returns true if the boolean is unknown.
 */
@Deprecated("UNIMPLEMENTED")
fun Boolean.is_unknown(): Boolean {
    throw VerikException()
}

/**
 * Returns a boolean set to X.
 */
@Deprecated("UNIMPLEMENTED")
fun x(type: Boolean): Boolean {
    throw VerikException()
}

/**
 * Returns a boolean set to Z.
 */
@Deprecated("UNIMPLEMENTED")
fun z(type: Boolean): Boolean {
    throw VerikException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Represents a value that is either true or false. Corresponds to the Kotlin type
// * [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/) and the SystemVerilog type logic.
// */
//class Boolean: Logic()
////////////////////////////////////////////////////////////////////////////////

/**
 * Returns the [Boolean] type.
 */
fun t_Boolean(): Boolean {
    throw VerikException()
}
