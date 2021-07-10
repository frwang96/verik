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

package count

import io.verik.core.*

var x = 0
var y = 0

fun f() {
    x = y + 1
    random()
    print("x=$x\n")
}

@Top
object Count : Module() {

    val clk = false
    val y = false

    @Seq
    fun update() {
        on (posedge(clk)) {}
    }

    @Com
    fun f() {}

    @Run
    fun g() {
        println()
        forever {
            println()
        }
    }
}

class Checker : Class()