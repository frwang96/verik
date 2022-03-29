/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A cover point within a [cover group][CoverGroup] that specifies a value to be covered. Construct a cover point with
 * the [cp] function.
 *
 *  ```
 *  class CG(
 *      @In var x: Boolean,
 *      @In var y: Boolean
 *  ) : CoverGroup() {
 *
 *      @Cover val cp_x = cp(x)
 *      @Cover val cp_y = cp(y)
 *  }
 *  ```
 */
class CoverPoint private constructor()
