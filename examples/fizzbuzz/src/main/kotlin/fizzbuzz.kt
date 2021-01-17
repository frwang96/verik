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
import verik.collection.*
import verik.data.*

@top class _fizzbuzz: _module() {

    private val array = _array(8, _int())

    @run fun main() {
        print_fizzbuzz(0)
        print_fizzbuzz(1)
        print_fizzbuzz(2)
        print_fizzbuzz(3)
        print_fizzbuzz(4)
        print_fizzbuzz(5)
        print_ubit(ubit(8, 0))
        println(array[0])
        val factorial = factorial()
        println("@${time()}: ${factorial.factorial(6)}")
    }

    private fun print_fizzbuzz(x: _int) {
        val fizz = (x % 3 == 0)
        val buzz = (x % 5 == 0)
        when {
            fizz && buzz -> println("fizzbuzz")
            fizz -> println("fizz")
            buzz -> println("buzz")
            else -> println(x)
        }
    }

    @task fun print_ubit(x: _ubit) {
        type(x, _ubit(8))
        delay(10)
        println(x)
    }
}

class _factorial: _class() {

    fun factorial(x: _int): _int {
        return if (x >= 2) factorial(x - 1) * x
        else 1
    }
}