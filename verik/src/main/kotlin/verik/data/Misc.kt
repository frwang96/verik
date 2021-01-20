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

/**
 * (UNIMPLEMENTED) Returns an list of [Int] from 0 to [n].
 */
fun range(n: Int): List<Int> {
    throw VerikDslException()
}

/**
 * Returns the concatenation of the arguments.
 */
fun cat(x: Data, vararg y: Data): Ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns [x] replicated [n] times.
 */
fun rep(n: Int, x: Data): Ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the maximum of its arguments.
 */
fun max(x: Int, vararg y: Int): Int {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the maximum of its arguments.
 */
fun max(x: Sbit, vararg y: Sbit): Sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the maximum of its arguments.
 */
fun max(x: Ubit, vararg y: Ubit): Ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the minimum of its arguments.
 */
fun min(x: Int, vararg y: Int): Int {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the minimum of its arguments.
 */
fun min(x: Sbit, vararg y: Sbit): Sbit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the minimum of its arguments.
 */
fun min(x: Ubit, vararg y: Ubit): Ubit {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the ceiling log base two of [x].
 */
fun log(x: Int): Int {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns the exponential base two of [x].
 */
fun exp(x: Int): Int {
    throw VerikDslException()
}