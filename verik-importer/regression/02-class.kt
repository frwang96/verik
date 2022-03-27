/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

open class c0 : Class()

open class c1 : c0()

open class c2<T, U, V : `*`> : Class()
