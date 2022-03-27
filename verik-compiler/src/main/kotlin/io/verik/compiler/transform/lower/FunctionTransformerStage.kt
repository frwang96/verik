/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object FunctionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionTransformerVisitor)
    }

    private object FunctionTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            when (val reference = callExpression.reference) {
                is BasicCoreFunctionDeclaration -> {
                    val targetFunctionDeclaration = reference.targetFunctionDeclaration
                    if (targetFunctionDeclaration != null)
                        callExpression.reference = targetFunctionDeclaration
                }
                is TransformableCoreFunctionDeclaration -> {
                    callExpression.replace(reference.transform(callExpression))
                }
                is CoreConstructorDeclaration -> {
                    val targetFunctionDeclaration = reference.targetFunctionDeclaration
                    if (targetFunctionDeclaration != null)
                        callExpression.reference = targetFunctionDeclaration
                }
            }
        }
    }
}
