/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    fun f0(): Boolean {
        return false
    }

    fun f1(x: Ubit<`8`>): Ubit<`8`> {
        return x.inv()
    }

    fun f2(x: Int = 0, y: Int = 0) {}

    @Run
    fun f3() {
        println(f0())
        println(f1(u0()))
        f2(0, 0)
        f2(0)
        f2()
    }
}