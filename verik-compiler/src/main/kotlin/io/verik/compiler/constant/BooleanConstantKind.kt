/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.constant

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
