/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

typealias t0 = Ubit<`8`>

var x0: t0 = imp()

typealias t1 = Queue<Boolean>

var x1: t1 = imp()

open class c : Class() {

    var x2: Boolean = imp()
}
