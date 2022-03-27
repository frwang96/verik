/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

enum class StageType {
    PARSE,
    PRE_CHECK,
    COMPILE,
    CAST,
    PRE_TRANSFORM,
    MID_CHECK,
    SPECIALIZE,
    RESOLVE,
    EVALUATE,
    INTERPRET,
    UPPER_TRANSFORM,
    LOWER_TRANSFORM,
    REORDER,
    POST_TRANSFORM,
    POST_CHECK,
    SERIALIZE;

    fun flushAfter(): Boolean {
        return this in listOf(
            COMPILE,
            CAST,
            INTERPRET,
            SERIALIZE
        )
    }

    fun checkNormalization(): Boolean {
        return this in listOf(
            PRE_TRANSFORM,
            SPECIALIZE,
            RESOLVE,
            EVALUATE,
            INTERPRET,
            UPPER_TRANSFORM,
            LOWER_TRANSFORM,
            REORDER,
            POST_TRANSFORM,
        )
    }
}
