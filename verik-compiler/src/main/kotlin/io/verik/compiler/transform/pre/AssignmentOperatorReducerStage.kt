/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object AssignmentOperatorReducerStage : ProjectStage() {

    private val assignmentOperatorMap = HashMap<KtBinaryOperatorKind, KtBinaryOperatorKind>()

    init {
        assignmentOperatorMap[KtBinaryOperatorKind.PLUS_EQ] = KtBinaryOperatorKind.PLUS
        assignmentOperatorMap[KtBinaryOperatorKind.MINUS_EQ] = KtBinaryOperatorKind.MINUS
        assignmentOperatorMap[KtBinaryOperatorKind.DIV_EQ] = KtBinaryOperatorKind.DIV
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(AssignmentOperatorReducerVisitor)
    }

    private object AssignmentOperatorReducerVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val kind = assignmentOperatorMap[binaryExpression.kind]
            if (kind != null) {
                val copiedLeft = ExpressionCopier.deepCopy(binaryExpression.left)
                val assignmentExpression = EKtBinaryExpression(
                    binaryExpression.location,
                    binaryExpression.type,
                    binaryExpression.left,
                    EKtBinaryExpression(
                        binaryExpression.location,
                        copiedLeft.type.copy(),
                        copiedLeft,
                        binaryExpression.right,
                        kind
                    ),
                    KtBinaryOperatorKind.EQ
                )
                binaryExpression.replace(assignmentExpression)
            }
        }
    }
}
