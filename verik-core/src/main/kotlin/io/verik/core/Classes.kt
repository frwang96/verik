/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * Simulation time. Corresponds to the SystemVerilog type time. The current simulation time can be found with the [time]
 * function.
 */
class Time

/**
 * A simulation event.
 */
class Event {

    /**
     * Trigger the event.
     */
    fun trigger() {
        throw VerikException()
    }
}
