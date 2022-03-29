/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

/**
 * Utility class to construct [cardinal declarations][CardinalDeclaration].
 */
object Cardinal {

    val UNRESOLVED = CardinalUnresolvedDeclaration

    fun of(value: Int): CardinalConstantDeclaration {
        return CardinalConstantDeclaration(value)
    }
}
