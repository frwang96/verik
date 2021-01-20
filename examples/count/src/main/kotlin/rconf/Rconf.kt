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

package rconf

import verik.data.*
import verik.rconf.*

fun main() {
    val even = i_RconfList("even")
    even.add(i_RconfEntry("0", u(8, 0), 3))
    even.add(i_RconfEntry("2", u(8, 2), 3))
    even.add(i_RconfEntry("4", u(8, 4), 3))

    val odd = i_RconfList("odd")
    odd.add(i_RconfEntry("1", u(8, 1), 3))
    odd.add(i_RconfEntry("3", u(8, 3), 3))
    odd.add(i_RconfEntry("5", u(8, 5), 3))

    val sanity = i_RconfList("sanity")
    sanity.add(even)
    sanity.add(odd)

   rconf_generate(sanity, t_Ubit(8))
}
