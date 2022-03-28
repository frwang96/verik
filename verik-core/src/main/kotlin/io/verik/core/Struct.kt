/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * The base class of all structs. They corresponds to SystemVerilog packed structs.
 *
 *  ```
 *  class S(
 *      var a: Boolean,
 *      var b: Boolean
 *  ) : Struct()
 *  ```
 */
abstract class Struct {

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
