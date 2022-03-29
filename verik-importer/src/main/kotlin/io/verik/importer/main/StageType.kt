/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

/**
 * Enum for stage type that is used to group the compiler stages.
 */
enum class StageType {
    PREPROCESS,
    PARSE,
    CAST,
    PRE_TRANSFORM,
    INTERPRET,
    POST_TRANSFORM,
    CHECK,
    SERIALIZE;

    fun flushAfter(): Boolean {
        return this in listOf(
            PREPROCESS,
            PARSE,
            CHECK,
            SERIALIZE
        )
    }

    fun checkNormalization(): Boolean {
        return this in listOf(
            CAST,
            PRE_TRANSFORM,
            INTERPRET,
            POST_TRANSFORM
        )
    }
}
