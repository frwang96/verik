/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    var x0: Boolean = nc()
    var x1: Ubit<`8`> = nc()
    var x2: Ubit<`8`> = nc()

    @Run
    fun f0() {
        println()
    }

    @Com
    var x3 = !x0

    var x4: Boolean = nc()

    @Com
    fun f1() {
        x4 = !x0
    }

    @Seq
    var x5 = oni(posedge(x0)) { x1 }

    @Seq
    fun f2() {
        on(posedge(x0)) {
            x2 = x1
        }
    }
}
