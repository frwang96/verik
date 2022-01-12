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

// M0.kt ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@SimTop
object M0 : Module() {

    @Make
    val m0 = M1(u0(), false, u(0x0), nc())

    @Make
    val m1 = M1(
        C = u0(),
        x0 = false,
        x1 = u(0x0),
        x2 = nc()
    )
}

// M1.kt ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

class M1(
    val C: Ubit<`8`>,
    @In var x0: Boolean,
    @In var x1: Ubit<`4`>,
    @Out var x2: Boolean
) : Module()
