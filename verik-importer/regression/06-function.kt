/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

fun f0(
    x0: Boolean
): Int = imp()

open class c : Class() {

    open fun f1(
        x1: Int
    ): Boolean = imp()

    open fun f2(
        x2: Ubit<`4`>,
        x3: Boolean = imp()
    ): Unit = imp()

    open fun f3(
        x4: Int,
        x5: Int
    ): Ubit<`4`> = imp()
}
