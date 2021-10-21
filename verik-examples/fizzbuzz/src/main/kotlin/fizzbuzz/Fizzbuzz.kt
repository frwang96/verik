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

class S(var value: Int) : Struct()

enum class E { A, B }

@Top
class Fizzbuzz : Module() {

    var x: Unpacked<`8`, Ubit<`8`>> = nc()
    val c = C()

    @Run
    fun main() {
        x = g(x)
        c.f()
    }

    fun g(x: Unpacked<`8`, Ubit<`8`>>): Unpacked<`8`, Ubit<`8`>> {
        return x
    }
}

class C {

    val x = false

    fun f() {
        this.x
    }
}
