/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.ResizableDeclarationContainer
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.Messages

abstract class EAbstractContainerComponent : EAbstractComponent(), ResizableDeclarationContainer {

    abstract var declarations: ArrayList<EDeclaration>

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return declarations.replaceIfContains(oldDeclaration, newDeclaration)
    }

    override fun insertChild(declaration: EDeclaration) {
        declaration.parent = this
        declarations.add(declaration)
    }

    override fun insertChildBefore(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        newDeclaration.parent = this
        val index = declarations.indexOf(oldDeclaration)
        if (index != -1) {
            declarations.add(index, newDeclaration)
        } else {
            Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not find declaration: ${oldDeclaration.name}")
        }
    }
}
