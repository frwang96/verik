/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    var s: S = nc()

    @Run
    fun f() {
        s = S(false, u0())
    }
}

class S(
    val x0: Boolean,
    val x1: Ubit<`8`>
) : Struct()