/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Randomization constraint for random properties of [classes][Class]. Constraints are annotated with [Cons] and
 * constructed with the [c] function.
 *
 *  ```
 *  class C : Class() {
 *
 *      @Rand
 *      var x = 0
 *
 *      @Cons
 *      val c = c(x > 0, x <= 6)
 *  }
 *  ```
 */
class Constraint private constructor() {

    /**
     * (UNIMPLEMENTED) Whether this randomization constraint is enabled.
     */
    var enabled: Boolean by VerikExceptionDelegate()
}
