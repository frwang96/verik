/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * A cover group that specifies a coverage model. Cover groups may contain [cover points][CoverPoint] and
 * [cover crosses][CoverCross].
 *
 *  ```
 *  class CG(
 *      @In var x: Boolean,
 *      @In var y: Boolean
 *  ) : CoverGroup() {
 *
 *      @Cover val cp_x = cp(x)
 *      @Cover val cp_y = cp(y)
 *
 *      @Cover val cc_x_y = cc(cp_x, cp_y)
 *  }
 *  ```
 */
abstract class CoverGroup {

    /**
     * Sample the cover group.
     */
    fun sample() {
        throw VerikException()
    }

    /**
     * Returns the coverage as a percentage value.
     */
    fun coverage(): Double {
        throw VerikException()
    }
}
