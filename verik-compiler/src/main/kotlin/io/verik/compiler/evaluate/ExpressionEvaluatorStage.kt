/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Most expression evaluation happens in [ExpressionEvaluatorSubstage] during specialization. This catches remaining
 * expressions that need to be evaluated.
 */
object ExpressionEvaluatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ExpressionEvaluatorVisitor)
    }

    private object ExpressionEvaluatorVisitor : TreeVisitor() {

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            val evaluatedExpression = ExpressionEvaluator.evaluate(expression)
            if (evaluatedExpression != null) {
                expression.replace(evaluatedExpression)
            }
        }
    }
}
