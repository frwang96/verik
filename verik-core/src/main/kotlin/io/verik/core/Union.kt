/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * The base class of all unions. They correspond to SystemVerilog packed unions. All members of the union must have the
 * same width.
 *
 *  ```
 *  class U(
 *      var a: Boolean,
 *      var b: Ubit<`1`>
 *  ) : Union()
 *  ```
 */
abstract class Union {

    /**
     * (UNIMPLEMENTED) Returns true if any bit is unknown.
     */
    fun hasx(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if any bit is floating.
     */
    fun hasz(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if any bit is unknown or floating.
     */
    fun hasxz(): Boolean {
        throw VerikException()
    }
}
