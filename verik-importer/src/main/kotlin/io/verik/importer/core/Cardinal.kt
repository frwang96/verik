/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.core

object Cardinal {

    fun of(value: Int): CardinalConstantDeclaration {
        return CardinalConstantDeclaration(value)
    }
}
