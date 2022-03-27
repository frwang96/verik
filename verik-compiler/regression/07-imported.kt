/*
 * SPDX-License-Identifier: Apache-2.0
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