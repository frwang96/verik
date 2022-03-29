/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.target.common.TargetFunctionDeclaration

/**
 * A core declaration that represents a constructor.
 */
class CoreConstructorDeclaration(
    override val parent: CoreDeclaration,
    val targetFunctionDeclaration: TargetFunctionDeclaration?
) : CoreDeclaration {

    override var name = parent.name
    override val signature: String = "fun $name(): $name"
}
