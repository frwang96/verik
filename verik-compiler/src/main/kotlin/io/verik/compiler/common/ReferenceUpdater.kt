/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.main.ProjectContext

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

    private class ReferenceUpdaterVisitor(private val referenceMap: Map<EDeclaration, EDeclaration>) : TreeVisitor() {

        private fun updateReference(receiverExpression: EReceiverExpression) {
            val updatedReference = referenceMap[receiverExpression.reference]
            if (updatedReference != null)
                receiverExpression.reference = updatedReference
        }

        private fun updateTypeReferences(type: Type) {
            val reference = referenceMap[type.reference]
            if (reference != null)
                type.reference = reference
            type.arguments.forEach { updateTypeReferences(it) }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            updateTypeReferences(typedElement.type)
            if (typedElement is EReceiverExpression) {
                updateReference(typedElement)
            }
            if (typedElement is ECallExpression) {
                typedElement.typeArguments.forEach { updateTypeReferences(it) }
            }
        }
    }
}
