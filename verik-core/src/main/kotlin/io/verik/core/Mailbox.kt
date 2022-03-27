/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * A mailbox of type [T] that is synchronized across processes. It corresponds to a SystemVerilog mailbox.
 */
class Mailbox<T> {

    /**
     * Put [value] into the mailbox.
     */
    fun put(value: T) {
        throw VerikException()
    }

    /**
     * Get the next value from the mailbox.
     */
    fun get(): T {
        throw VerikException()
    }
}
