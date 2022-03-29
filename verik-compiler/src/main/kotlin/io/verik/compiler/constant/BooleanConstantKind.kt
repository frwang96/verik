/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

/**
 * Enum for four-state boolean value.
 */
enum class BooleanConstantKind {
    TRUE,
    FALSE,
    UNKNOWN,
    FLOATING;

    fun isUnknownOrFloating(): Boolean {
        return (this == UNKNOWN) || (this == FLOATING)
    }

    companion object {

        fun fromBoolean(boolean: Boolean): BooleanConstantKind {
            return if (boolean) TRUE else FALSE
        }
    }
}
