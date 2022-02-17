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

// Imported0.kt ////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

package imported

import io.verik.core.*

val x0: Boolean = imp()

class C0 : Class()

// Imported1.kt ////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

package imported.test_pkg

import io.verik.core.*

class C1<T> : Class() {

    companion object {

        fun <T> f0(): Boolean = imp()
    }
}

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import imported.C0
import imported.x0
import imported.test_pkg.C1
import io.verik.core.*

@Entry
object M : Module() {

    @Run
    fun f() {
        val x1 = x0
        val c0 = C0()
        val c1 = C1<Int>()
        val x2 = C1.f0<Int>()
    }
}