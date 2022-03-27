/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeReferenceTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeReferenceTransformerVisitor)
    }

    private object TypeReferenceTransformerVisitor : TreeVisitor() {

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            transform(typedElement.type, typedElement)
            if (typedElement is EAbstractClass) transform(typedElement.superType, typedElement)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments.forEach { transform(it, callExpression) }
        }

        override fun visitScopeExpression(scopeExpression: EScopeExpression) {
            super.visitScopeExpression(scopeExpression)
            transform(scopeExpression.scope, scopeExpression)
        }

        private fun transform(type: Type, element: EElement) {
            type.arguments.forEach { transform(it, element) }
            val reference = type.reference
            if (reference is CoreClassDeclaration) {
                val targetClassDeclaration = reference.targetClassDeclaration
                if (targetClassDeclaration != null) {
                    type.reference = targetClassDeclaration
                } else {
                    Messages.INTERNAL_ERROR.on(element, "Unable to transform type reference : ${reference.name}")
                }
            }
        }
    }
}
