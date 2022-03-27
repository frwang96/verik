/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

typealias t = Ubit<`4`>

class s(
    var x0: Int,
    var x1: Time,
    var x2: t
) : Struct()

var x3: s = imp()