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

@file:Suppress("UNUSED_PARAMETER")

package verik.common.data

import verik.common.*

typealias _int = Int

operator fun Int.Companion.invoke(): _int {
    throw VerikDslException()
}

infix fun _int.init(x: _int) {
    throw VerikDslException()
}

fun _int.is_unknown(): Boolean {
    throw VerikDslException()
}

fun _int.pack(): _uint {
    throw VerikDslException()
}
