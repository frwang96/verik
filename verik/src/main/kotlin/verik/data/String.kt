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
infix fun String.init(x: String) {
    throw VerikDslException()
}
//////////////////////////////////////////////////////////////////////////////// DOKKA
///**
// * Represents a character string. Corresponds to the Kotlin type
// * [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/) and the SystemVerilog type string.
// */
//class String: Instance()
////////////////////////////////////////////////////////////////////////////////

/**
 * [String] type.
 */
@Suppress("FunctionName")
fun t_String(): String {
    throw VerikDslException()
}
