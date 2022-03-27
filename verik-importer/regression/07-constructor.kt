/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

open class c0 : Class {

    constructor(
        x0: Boolean,
        x1: Boolean
    ) : super()
}

open class c1 : c0 {

    constructor(
        x2: Boolean,
        x3: Boolean
    ) : super(imp(), imp())
}