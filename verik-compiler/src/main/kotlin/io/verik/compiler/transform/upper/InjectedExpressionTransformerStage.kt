/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.sv.EConstraint
import io.verik.compiler.ast.element.declaration.sv.ECoverBin
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object InjectedExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(InjectedExpressionTransformerVisitor)
    }

    private object InjectedExpressionTransformerVisitor : TreeVisitor() {

        private fun getInjectedExpression(expression: EExpression): EInjectedExpression? {
            if (expression is EStringTemplateExpression) {
                return EInjectedExpression(
                    expression.location,
                    expression.entries
                )
            } else if (expression is ECallExpression && expression.reference == Core.Kt.Text.F_trimIndent) {
                val receiver = expression.receiver!!
                if (receiver is EStringTemplateExpression) {
                    return EInjectedExpression(
                        expression.location,
                        StringEntry.trimIndent(receiver.entries)
                    )
                }
            }
            Messages.ILLEGAL_INJECTED_EXPRESSION.on(expression)
            return null
        }

        override fun visitCoverBin(coverBin: ECoverBin) {
            val injectedExpression = getInjectedExpression(coverBin.expression)
            if (injectedExpression != null) coverBin.expression.replace(injectedExpression)
        }

        override fun visitConstraint(constraint: EConstraint) {
            super.visitConstraint(constraint)
            constraint.body.statements.forEach {
                if (it.type.reference == Core.Kt.C_String) {
                    val injectedExpression = getInjectedExpression(it)
                    if (injectedExpression != null) it.replace(injectedExpression)
                }
            }
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference in listOf(Core.Vk.F_inj_String, Core.Vk.F_inji_String)) {
                val injectedExpression = getInjectedExpression(callExpression.valueArguments[0])
                if (injectedExpression != null) callExpression.replace(injectedExpression)
            }
        }
    }
}
