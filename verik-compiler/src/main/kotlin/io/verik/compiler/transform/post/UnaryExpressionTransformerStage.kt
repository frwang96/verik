/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object UnaryExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UnaryExpressionTransformerVisitor)
    }

    private object UnaryExpressionTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is UnaryCoreFunctionDeclaration) {
                val kind = reference.kind
                callExpression.replace(
                    ESvUnaryExpression(
                        callExpression.location,
                        callExpression.type,
                        callExpression.receiver!!,
                        kind
                    )
                )
            }
        }

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            val kind = when (unaryExpression.kind) {
                KtUnaryOperatorKind.PRE_INC -> SvUnaryOperatorKind.PRE_INC
                KtUnaryOperatorKind.PRE_DEC -> SvUnaryOperatorKind.PRE_DEC
                KtUnaryOperatorKind.POST_INC -> SvUnaryOperatorKind.POST_INC
                KtUnaryOperatorKind.POST_DEC -> SvUnaryOperatorKind.POST_DEC
                else -> null
            }
            if (kind != null) {
                unaryExpression.replace(
                    ESvUnaryExpression(
                        unaryExpression.location,
                        unaryExpression.type,
                        unaryExpression.expression,
                        kind
                    )
                )
            }
        }
    }
}
