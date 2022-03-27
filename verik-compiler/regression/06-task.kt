/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    @Task
    fun f0() {
        delay(1)
    }

    @Task
    fun f1(x: Ubit<`8`>): Ubit<`8`> {
        delay(1)
        return x.inv()
    }

    @Run
    fun f2() {
        f0()
        val x = f1(u0())
        println(x)
    }
}