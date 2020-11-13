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

package stubs

import verik.common.data.*
import verik.common.stubs.*

fun main(args: Array<String>) {
    val even = StubList("even", listOf(
            StubEntry("0", uint(8, 0), 3),
            StubEntry("2", uint(8, 2), 3),
            StubEntry("4", uint(8, 4), 3)
    ))
    val odd = StubList("odd", listOf(
            StubEntry("1", uint(8, 1), 3),
            StubEntry("3", uint(8, 3), 3),
            StubEntry("5", uint(8, 5), 3)
    ))
    val sanity = StubList("sanity", listOf(even, odd))
    writeStubs(args, _uint(8), listOf(sanity))
}
