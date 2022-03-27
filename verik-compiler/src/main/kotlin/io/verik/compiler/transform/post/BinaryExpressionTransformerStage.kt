/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.BinaryCoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object BinaryExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BinaryExpressionTransformerVisitor)
    }

    private object BinaryExpressionTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is BinaryCoreFunctionDeclaration) {
                val kind = reference.kind
                callExpression.replace(
                    ESvBinaryExpression(
                        callExpression.location,
                        callExpression.type,
                        callExpression.receiver!!,
                        callExpression.valueArguments[0],
                        kind
                    )
                )
            }
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val kind = when (binaryExpression.kind) {
                KtBinaryOperatorKind.LT -> SvBinaryOperatorKind.LT
                KtBinaryOperatorKind.LTEQ -> SvBinaryOperatorKind.LTEQ
                KtBinaryOperatorKind.GT -> SvBinaryOperatorKind.GT
                KtBinaryOperatorKind.GTEQ -> SvBinaryOperatorKind.GTEQ
                KtBinaryOperatorKind.EQEQ -> SvBinaryOperatorKind.EQEQ
                KtBinaryOperatorKind.EXCL_EQ -> SvBinaryOperatorKind.EXCL_EQ
                KtBinaryOperatorKind.ANDAND -> SvBinaryOperatorKind.ANDAND
                KtBinaryOperatorKind.OROR -> SvBinaryOperatorKind.OROR
                else -> null
            }
            if (kind != null) {
                binaryExpression.replace(
                    ESvBinaryExpression(
                        binaryExpression.location,
                        binaryExpression.type,
                        binaryExpression.left,
                        binaryExpression.right,
                        kind
                    )
                )
            }
        }
    }
}
