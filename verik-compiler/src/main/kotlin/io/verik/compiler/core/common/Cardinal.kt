/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

object Cardinal {

    val UNRESOLVED = CardinalUnresolvedDeclaration
    val FALSE = of(0)
    val TRUE = of(1)

    fun of(value: Int): CardinalConstantDeclaration {
        return CardinalConstantDeclaration(value)
    }
}
