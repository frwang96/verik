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

// M.kt ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@SimTop
object M : Module() {

    @Task
    fun f0() {
        delay(1)
    }

    @Task
    fun f1(x: Ubit<`8`>): Ubit<`8`> {
        delay(1)
        return x.invert()
    }

    @Run
    fun f2() {
        f0()
        val x = f1(u0())
        println(x)
    }
}