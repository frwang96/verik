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

import verik.data.*
import verik.stubs.*

fun main(args: Array<String>) {
    val even = stub_list("even")
    even.add(stub_entry("0", uint(8, 0), 3))
    even.add(stub_entry("2", uint(8, 2), 3))
    even.add(stub_entry("4", uint(8, 4), 3))

    val odd = stub_list("odd")
    odd.add(stub_entry("1", uint(8, 1), 3))
    odd.add(stub_entry("3", uint(8, 3), 3))
    odd.add(stub_entry("5", uint(8, 5), 3))

    val sanity = stub_list("sanity")
    sanity.add(even)
    sanity.add(odd)

    generate_stubs(args, sanity, _uint(8))
}
