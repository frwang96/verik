/*
 * Copyright (c) 2021 Francis Wang
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

package adder

import io.verik.core.*

typealias WIDTH = `8`

@Top
class AdderTop : Module() {

    var a: Ubit<WIDTH> = nc()
    var b: Ubit<WIDTH> = nc()
    var x: Ubit<WIDTH> = nc()

    @Make
    val adder = Adder<WIDTH>(a, b, x)

    @Run
    fun test() {
        repeat(64) {
            a = randomUbit()
            b = randomUbit()
            delay(1)
            val expected = a + b
            if (x == expected) print("PASS ")
            else print("FAIL ")
            println("$a + $b = $x")
        }
    }
}
