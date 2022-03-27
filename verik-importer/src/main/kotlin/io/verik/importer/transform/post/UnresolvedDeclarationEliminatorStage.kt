/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.post

import io.verik.importer.ast.element.declaration.EContainerDeclaration
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object UnresolvedDeclarationEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val unresolvedDeclarations = HashSet<EDeclaration>()
        val declarationResolvedIndexerVisitor = DeclarationResolvedIndexerVisitor(unresolvedDeclarations)
        var unresolvedDeclarationsCount: Int
        do {
            unresolvedDeclarationsCount = unresolvedDeclarations.size
            projectContext.project.accept(declarationResolvedIndexerVisitor)
        } while (unresolvedDeclarationsCount != unresolvedDeclarations.size)
    }

    private class DeclarationResolvedIndexerVisitor(
        private val unresolvedDeclarations: HashSet<EDeclaration>
    ) : TreeVisitor() {

        override fun visitContainerDeclaration(containerDeclaration: EContainerDeclaration) {
            super.visitContainerDeclaration(containerDeclaration)
            val declarations = ArrayList<EDeclaration>()
            containerDeclaration.declarations.forEach {
                val declarationResolvedCheckerVisitor = DeclarationResolvedCheckerVisitor(unresolvedDeclarations)
                it.accept(declarationResolvedCheckerVisitor)
                if (declarationResolvedCheckerVisitor.isResolved) {
                    declarations.add(it)
                } else {
                    unresolvedDeclarations.add(it)
                }
            }
            containerDeclaration.declarations = declarations
        }
    }

    private class DeclarationResolvedCheckerVisitor(
        private val unresolvedDeclarations: HashSet<EDeclaration>
    ) : TreeVisitor() {

        var isResolved = true

        override fun visitDescriptor(descriptor: EDescriptor) {
            if (!isResolved) return
            super.visitDescriptor(descriptor)
            if (!descriptor.type.isResolved() || descriptor.type.reference in unresolvedDeclarations) {
                isResolved = false
            }
        }
    }
}
