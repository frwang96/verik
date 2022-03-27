/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A cover point within a [cover group][CoverGroup] that specifies a value to be covered.
 */
abstract class CoverPoint {

    /**
     * Create a cover bin. [value] will be injected as SystemVerilog and used to specify the bin.
     */
    fun bin(name: String, value: String) {
        throw VerikException()
    }

    /**
     * Create an array of cover bins. [value] will be injected as SystemVerilog and used to specify the bins.
     */
    fun bins(name: String, value: String) {
        throw VerikException()
    }

    /**
     * Ignore a bin. [value] will be injected as SystemVerilog and used to specify the bin.
     */
    fun ignoreBin(name: String, value: String) {
        throw VerikException()
    }
}
