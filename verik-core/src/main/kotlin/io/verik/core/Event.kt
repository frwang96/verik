/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

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
