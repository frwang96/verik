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

    val x: Unpacked<`8`, Ubit<`8`>> = nc()
    val y: Unpacked<`8`, S> = nc()
    val z: Ubit<`3`> = nc()

    @Suppress("unused")
    val c = C()

    @Run
    fun main() {
        val e = E.A
        f(0)
        repeat(3) {
            println(g(0))
        }
        x[0] = zeroes()
        x[z] = zeroes()
        y[0].value = 0
        println(E.A)
        println("$e ${E.B}")
    }

    fun f(x: Int) {
        println(x)
    }

    fun g(x: Int): Int {
        println(y.size)
        for (it in 0 until 8) {
            println(it)
        }
        return x + 1
    }
}

class C
