/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.common

import io.verik.importer.ast.common.DeclarationContainer
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.common.Visitor
import io.verik.importer.common.replaceIfContains
import io.verik.importer.message.SourceLocation

class EProject(
    var declarations: ArrayList<EDeclaration>
) : EElement(), DeclarationContainer {

    init {
        declarations.forEach { it.parent = this }
    }

    override val location: SourceLocation = SourceLocation.NULL

    override fun accept(visitor: Visitor) {
        visitor.visitProject(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return declarations.replaceIfContains(oldDeclaration, newDeclaration)
    }
}
