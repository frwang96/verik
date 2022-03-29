/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks for references that have not been transformed to SystemVerilog.
 */
object UntransformedReferenceCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UntransformedReferenceCheckerVisitor)
    }

    private object UntransformedReferenceCheckerVisitor : TreeVisitor() {

        private const val message = "Reference has not been transformed to SystemVerilog"

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkType(typedElement.type, typedElement)
            if (typedElement is EAbstractClass) checkType(typedElement.superType, typedElement)
        }

        override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
            super.visitReceiverExpression(receiverExpression)
            if (receiverExpression.reference is CoreDeclaration)
                Messages.INTERNAL_ERROR.on(
                    receiverExpression,
                    "$message : ${receiverExpression.reference.name}"
                )
        }

        private fun checkType(type: Type, element: EElement) {
            type.arguments.forEach { checkType(it, element) }
            if (type.reference is CoreDeclaration) {
                Messages.INTERNAL_ERROR.on(element, "$message : ${type.reference.name}")
            }
        }
    }
}
