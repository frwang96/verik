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

package rconf

import verik.data.*
import verik.rconf.*

fun main() {
    val even = rconf_list("even")
    even.add(rconf_entry("0", ubit(8, 0), 3))
    even.add(rconf_entry("2", ubit(8, 2), 3))
    even.add(rconf_entry("4", ubit(8, 4), 3))

    val odd = rconf_list("odd")
    odd.add(rconf_entry("1", ubit(8, 1), 3))
    odd.add(rconf_entry("3", ubit(8, 3), 3))
    odd.add(rconf_entry("5", ubit(8, 5), 3))

    val sanity = rconf_list("sanity")
    sanity.add(even)
    sanity.add(odd)

   rconf_generate(sanity, _ubit(8))
}
