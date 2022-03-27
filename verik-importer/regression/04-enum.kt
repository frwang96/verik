/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

enum class e0 {
    E0,
    E1,
    E2
}

var x0: e0 = imp()

open class c : Class() {

    enum class e1 {
        E3,
        E4
    }

    var x1: e1 = imp()
}
