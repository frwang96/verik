/*
 * SPDX-License-Identifier: Apache-2.0
 */

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

class m0 : Module()

class m1(
    @In var x0: Boolean,
    @In var x1: Ubit<`8`>,
    @Out var x2: Packed<`2`, Ubit<`4`>>
) : Module()

class m2(
    @In var x3: Boolean,
    @In var x4: Ubit<`8`>
) : Module()
