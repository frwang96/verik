/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
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

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            updateTypeReferences(typedElement.type)
            when (typedElement) {
                is EAbstractClass -> {
                    updateTypeReferences(typedElement.superType)
                }
                is EKtFunction -> {
                    typedElement.overriddenFunction = typedElement.overriddenFunction?.let { updateReference(it) }
                }
                is EReferenceExpression -> {
                    typedElement.reference = updateReference(typedElement.reference)
                }
                is ECallExpression -> {
                    typedElement.reference = updateReference(typedElement.reference)
                    typedElement.typeArguments.forEach { updateTypeReferences(it) }
                }
            }
        }
    }
}
