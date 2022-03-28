/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Randomization constraint for random properties of [classes][Class].
 */
class Constraint private constructor() {

    /**
     * (UNIMPLEMENTED) Whether this randomization constraint is enabled.
     */
    var enabled: Boolean by VerikExceptionDelegate()
}
