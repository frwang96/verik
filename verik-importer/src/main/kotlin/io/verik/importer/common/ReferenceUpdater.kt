/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.importer.common

import io.verik.importer.ast.common.Declaration
import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.expression.EReferenceExpression
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
            if (element is EReferenceExpression) {
                element.reference = updateReference(element.reference)
            }
        }
    }
}
