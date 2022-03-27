/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.core

class CardinalConstantDeclaration(
    val value: Int
) : CardinalDeclaration {

    override val name = "`$value`"

    override fun equals(other: Any?): Boolean {
        return (other is CardinalConstantDeclaration) && (other.value == value)
    }

    override fun hashCode(): Int {
        return value
    }
}
