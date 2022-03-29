/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

import io.verik.importer.ast.element.declaration.EContainerDeclaration
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.common.ElementCopier
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that substitutes local type aliases. Local type aliases are not permitted in Kotlin.
 */
object LocalTypeAliasEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(LocalTypeAliasSubstitutorVisitor)
        projectContext.project.accept(LocalTypeAliasEliminatorVisitor)
    }

    private object LocalTypeAliasSubstitutorVisitor : TreeVisitor() {

        override fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
            super.visitReferenceDescriptor(referenceDescriptor)
            val reference = referenceDescriptor.reference
            if (reference is ETypeAlias) {
                val parent = reference.parent
                if (parent is ESvClass || parent is EModule) {
                    val descriptor = ElementCopier.deepCopy(reference.descriptor, referenceDescriptor.location)
                    referenceDescriptor.replace(descriptor)
                }
            }
        }
    }

    private object LocalTypeAliasEliminatorVisitor : TreeVisitor() {

        override fun visitContainerDeclaration(containerDeclaration: EContainerDeclaration) {
            super.visitContainerDeclaration(containerDeclaration)
            if (containerDeclaration is ESvClass || containerDeclaration is EModule) {
                val declarations = containerDeclaration.declarations.filter { it !is ETypeAlias }
                containerDeclaration.declarations = ArrayList(declarations)
            }
        }
    }
}
