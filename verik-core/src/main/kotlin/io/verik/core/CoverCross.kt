/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A cover cross within a [cover group][CoverGroup] that crosses a number of [cover points][CoverPoint].
 */
abstract class CoverCross {

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
