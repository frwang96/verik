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

    var w: Boolean = nc()
    val x: Unpacked<`8`, Ubit<`8`>> = nc()
    val y: Unpacked<`8`, S> = nc()
    var z: Ubit<`3`> = nc()

    @Suppress("unused")
    val c = C()

    @Make
    val mi = MI()

    @Make
    val cb = CB(posedge(false), z)

    @Run
    fun main() {
        val h = h(z)
        println(h)
        f(0)
        repeat(3) {
            println(g(0))
        }
        x[0] = u0()
        x[z] = u0()
        x[0] = z.uext()
        x[0] = u(z.sext())
        y[0].value = 0
        z++
        println(E.A)
        val e = E.A
        println("$e ${E.B}")
        random(1)
        random(1, 2)
        z[0] = u("3'b000")
        z = rep<`3`>(true)
        z = z srl 1
        z = z sra 1
        z = x[0].tru()
        z = z.invert()
        z = z.reverse()
        w = s(z) < s(z)
        println(cb.z)
    }

    @Task
    fun f(x: Int) {
        println(x)
        var a = when (w) {
            true -> 0
            false -> 1
        }
        a = if (a == 0) {
            println()
            1
        } else 2
        println(a)
    }

    fun g(x: Int): Int {
        println(y.size)
        for (it in 0 until 8) {
            println(it)
        }
        return x + 1
    }
}

class MI : ModuleInterface()

class CB(
    override val event: Event,
    @In val z: Ubit<`3`>
) : ClockingBlock()

fun h(x: Ubit<`3`>): Ubit<`3`> {
    return x
}

class C
