/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.target.common.TargetClassDeclaration

/**
 * A core declaration that represents a class.
 */
class CoreClassDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    val superClass: CoreClassDeclaration?,
    val targetClassDeclaration: TargetClassDeclaration?
) : CoreDeclaration {

    override val signature: String = "class $name"
}
