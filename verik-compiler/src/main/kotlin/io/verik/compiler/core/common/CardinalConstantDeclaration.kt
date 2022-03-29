/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

/**
 * A cardinal declaration that represents the integer [value].
 */
class CardinalConstantDeclaration(
    val value: Int
) : CardinalDeclaration {

    override var name = "`$value`"

    override fun equals(other: Any?): Boolean {
        return (other is CardinalConstantDeclaration) && (other.value == value)
    }

    override fun hashCode(): Int {
        return value
    }
}
