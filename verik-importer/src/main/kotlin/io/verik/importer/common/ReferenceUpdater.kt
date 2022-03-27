/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

import io.verik.importer.ast.common.Declaration
import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.main.ProjectContext

class ReferenceUpdater(val projectContext: ProjectContext) {

    private val referenceMap = HashMap<EDeclaration, EDeclaration>()

    fun replace(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        val parent = oldDeclaration.parentNotNull()
        parent.replaceChildAsDeclarationContainer(oldDeclaration, newDeclaration)
        referenceMap[oldDeclaration] = newDeclaration
    }

    fun update(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        referenceMap[oldDeclaration] = newDeclaration
    }

    fun flush() {
        val referenceUpdaterVisitor = ReferenceUpdaterVisitor(referenceMap)
        projectContext.project.accept(referenceUpdaterVisitor)
        referenceMap.clear()
    }

    private class ReferenceUpdaterVisitor(
        private val referenceMap: Map<EDeclaration, EDeclaration>
    ) : TreeVisitor() {

        private fun updateReference(reference: Declaration): Declaration {
            return referenceMap[reference] ?: reference
        }

        private fun updateTypeReferences(type: Type) {
            type.arguments.forEach { updateTypeReferences(it) }
            type.reference = updateReference(type.reference)
        }

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDescriptor) {
                updateTypeReferences(element.type)
            }
            if (element is EReferenceDescriptor) {
                element.reference = updateReference(element.reference)
            }
        }
    }
}
