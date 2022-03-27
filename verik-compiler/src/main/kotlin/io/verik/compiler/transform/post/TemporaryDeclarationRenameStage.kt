/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object TemporaryDeclarationRenameStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val temporaryDeclarationRenameVisitor = TemporaryDeclarationRenameVisitor()
        projectContext.project.accept(temporaryDeclarationRenameVisitor)
    }

    private class TemporaryDeclarationRenameVisitor : TreeVisitor() {

        private var index = 0

        override fun visitDeclaration(declaration: EDeclaration) {
            if (declaration.name == "<tmp>") {
                declaration.name = "__$index"
                index++
            }
            super.visitDeclaration(declaration)
        }
    }
}
