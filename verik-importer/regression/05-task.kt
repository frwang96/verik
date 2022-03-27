/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

@Task
fun t0(
    x0: Boolean
): Unit = imp()

open class c : Class() {

    @Task
    open fun t1(
        x1: Int
    ): Unit = imp()

    @Task
    open fun t2(
        x2: Ubit<`4`>,
        x3: Boolean
    ): Unit = imp()

    @Task
    open fun t3(
        x4: Int,
        x5: Int
    ): Unit = imp()
}
