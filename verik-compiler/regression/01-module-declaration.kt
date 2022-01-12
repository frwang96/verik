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

    var x0: Boolean = nc()
    var x1: Ubit<`8`> = nc()
    var x2: Ubit<`8`> = nc()

    @Run
    fun f0() {
        println()
    }

    @Seq
    fun f1() {
        on(posedge(x0)) {
            x1 = x2
        }
    }

    @Com
    var x3 = !x0

    var x4: Boolean = nc()

    @Com
    fun f2() {
        x4 = !x0
    }
}
