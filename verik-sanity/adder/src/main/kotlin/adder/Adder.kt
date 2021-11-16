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

package adder

import io.verik.core.*

class Adder<N : `*`>(
    @In var a: Ubit<N>,
    @In var b: Ubit<N>,
    @Out var x: Ubit<N>
) : Module() {

    fun fullAdder(a: Boolean, b: Boolean, c: Boolean): Ubit<`2`> {
        val x: Ubit<`2`> = u0()
        x[0] = a xor b xor c
        x[1] = (a && b) || (a && c) || (b && c)
        return x
    }

    @Com
    fun f() {
        var c = false
        for (i in 0 until i<N>()) {
            val fa = fullAdder(a[i], b[i], c)
            x[i] = fa[0]
            c = fa[1]
        }
    }
}
