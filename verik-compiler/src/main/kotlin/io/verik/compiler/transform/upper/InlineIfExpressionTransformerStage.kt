/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.sv.EInlineIfExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that transforms if expressions to SystemVerilog inline if expressions if suitable.
 */
object InlineIfExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(InlineIfExpressionTransformerVisitor)
    }

    private object InlineIfExpressionTransformerVisitor : TreeVisitor() {

        private fun reduceExpression(expression: EBlockExpression?): EExpression? {
            return if (expression?.statements?.size == 1) {
                expression.statements.first()
            } else null
        }

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            if (ifExpression.getExpressionKind().isSubexpression()) {
                val reducedThenExpression = reduceExpression(ifExpression.thenExpression)
                val reducedElseExpression = reduceExpression(ifExpression.elseExpression)
                if (reducedThenExpression != null && reducedElseExpression != null) {
                    val inlineIfExpression = EInlineIfExpression(
                        ifExpression.location,
                        ifExpression.type,
                        ifExpression.condition,
                        reducedThenExpression,
                        reducedElseExpression
                    )
                    ifExpression.replace(inlineIfExpression)
                }
            }
        }
    }
}
