/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.DeclarationContainer
import io.verik.importer.common.Visitor
import io.verik.importer.common.replaceIfContains

/**
 * Abstract declaration element that contains other [declarations].
 */
abstract class EContainerDeclaration : EDeclaration(), DeclarationContainer {

    abstract var declarations: ArrayList<EDeclaration>

    override fun acceptChildren(visitor: Visitor) {
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return declarations.replaceIfContains(oldDeclaration, newDeclaration)
    }
}
