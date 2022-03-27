/*
 * SPDX-License-Identifier: Apache-2.0
 */

// Test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

@file:Verik

import io.verik.core.*

@Entry
object M : Module() {

    val x0 = C0()
    val x1 = C1()
    val x2 = C2<Int>()
    val x3 = C3()
    val x4 = C4(false)

    @Run
    fun f0() {
        println(C0.x5)
        println(C2.x6)
    }
}

open class C0 : Class() {

    companion object {

        var x5 = false
    }
}

class C1: C0()

class C2<T> : Class() {

    companion object {

        var x6 = false
    }
}

class C3: C0 {

    constructor(): super()
}

class C4(x7: Boolean) : Class() {

    var x8 = false

    init {
        x8 = x7
    }
}
