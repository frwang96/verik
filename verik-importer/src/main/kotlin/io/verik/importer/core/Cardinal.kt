/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.core

/**
 * Utility class to construct [cardinal declarations][CardinalDeclaration].
 */
object Cardinal {

    fun of(value: Int): CardinalConstantDeclaration {
        return CardinalConstantDeclaration(value)
    }
}
