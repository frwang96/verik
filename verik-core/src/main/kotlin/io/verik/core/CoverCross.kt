/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A cover cross within a [cover group][CoverGroup] that crosses a number of [cover points][CoverPoint]. Construct a
 * cover cross with the [cc] function.
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
class CoverCross private constructor()
