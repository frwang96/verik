/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    val e: E = nc()

    @Run
    fun f() {
        if (e == E.E0) {
            println(E.E1)
        }
    }
}

enum class E {
    E0, E1, E2
}