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

import verik.base.*
import verik.collection.Array
import verik.data.*

typealias Byte = Ubit<_8>

@top object FizzBuzz: Module() {

    val data: Byte = u(0)
    val array = Array<_8, Int>()

    @run fun main() {
        print_fizzbuzz(0)
        print_fizzbuzz(1)
        print_fizzbuzz(2)
        print_fizzbuzz(3)
        print_fizzbuzz(4)
        print_fizzbuzz(5)
        print_ubit(u(0))
        print_byte(data)
        println(array[0])
        val factorial = Factorial(6)
        println("@${time()}: ${factorial.factorial()}")
    }

    fun print_fizzbuzz(x: Int) {
        val fizz = (x % 3 == 0)
        val buzz = (x % 5 == 0)
        when {
            fizz && buzz -> println("fizzbuzz")
            fizz -> println("fizz")
            buzz -> println("buzz")
            else -> println(x)
        }
    }

    @task fun print_ubit(x: Ubit<_8>) {
        delay(10)
        println(x)
    }

    fun print_byte(x: Byte) {
        println(x)
    }
}

class Factorial(val x: Int): Class() {

    fun factorial(): Int {
        var n = 1
        for (i in range(x)) n *= (i + 1)
        return n
    }
}