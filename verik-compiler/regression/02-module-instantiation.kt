/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test0.kt ////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
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

// Test1.kt ////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

class M1(
    @In val C: Ubit<`8`>,
    @In var x0: Boolean,
    @In var x1: Ubit<`4`>,
    @Out var x2: Boolean
) : Module()
