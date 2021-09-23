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

package fizzbuzz

import io.verik.core.*

@Top
class Fizzbuzz : Module() {

    @Suppress("unused")
    val x: Unpacked<`8`, Unpacked<`16`, Boolean>> = nc()

    @Suppress("unused")
    val c = C()

    @Run
    fun main() {
        f(0)
        println(g(0))
    }

    fun f(x: Int) {
        println(x)
    }

    fun g(x: Int): Int {
        return x + 1
    }
}

class C
