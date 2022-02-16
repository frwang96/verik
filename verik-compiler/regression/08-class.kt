/*
 * Copyright (c) 2022 Francis Wang
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

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    val x0 = C0()
    val x1 = C1()
    val x2 = C2<Int>()
    val x3 = C3()

    @Run
    fun f0() {
        println(C0.x4)
        println(C2.x5)
    }
}

open class C0 {

    companion object {

        var x4 = false
    }
}

class C1: C0()

class C2<T> {

    companion object {

        var x5 = false
    }
}

class C3: C0 {

    constructor(): super()
}
