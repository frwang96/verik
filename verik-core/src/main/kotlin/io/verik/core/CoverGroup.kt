/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * A cover group that specifies a coverage model. Cover groups may contain [cover points][CoverPoint] and
 * [cover crosses][CoverCross].
 */
abstract class CoverGroup {

    /**
     * Sample the cover group.
     */
    fun sample() {
        throw VerikException()
    }

    /**
     * Returns the coverage as a percentage value.
     */
    fun getCoverage() {
        throw VerikException()
    }
}
