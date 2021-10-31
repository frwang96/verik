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

    @Run
    fun main() {
        println(E.e)
        E.f()
        val a = ArrayList<C>()
        println(a.size)
    }
}

class C

object E {

    val e = 0

    fun f() {}
}
