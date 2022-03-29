/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

/**
 * A cardinal declaration that represents a function on other cardinals.
 */
class CoreCardinalFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String
) : CoreDeclaration, CardinalDeclaration {

    override val signature: String = "typealias $name"
}
