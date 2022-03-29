/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks that type arguments that expect a cardinal type are satisfied.
 */
object TypeArgumentTypeCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeArgumentTypeCheckerVisitor)
    }

    private object TypeArgumentTypeCheckerVisitor : TreeVisitor() {

        private fun checkType(type: Type, element: EElement) {
            if (type.reference is CoreCardinalFunctionDeclaration) {
                type.arguments.forEach {
                    if (!it.isCardinalType()) {
                        Messages.EXPECTED_CARDINAL_TYPE.on(element, it)
                    }
                }
            }
            type.arguments.forEach { checkType(it, element) }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkType(typedElement.type, typedElement)
        }

        override fun visitTypeAlias(typeAlias: ETypeAlias) {}

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments.forEach { checkType(it, callExpression) }
        }
    }
}
