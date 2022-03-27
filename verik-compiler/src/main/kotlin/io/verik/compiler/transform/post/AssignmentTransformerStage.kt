/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlock
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.expression.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object AssignmentTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val assignmentTransformerVisitor = AssignmentTransformerVisitor()
        projectContext.project.accept(assignmentTransformerVisitor)
    }

    private class AssignmentTransformerVisitor : TreeVisitor() {

        private var inAlwaysSeqBlock = false

        private fun getKind(binaryExpression: EKtBinaryExpression): SvBinaryOperatorKind {
            if (inAlwaysSeqBlock) {
                var expression: EExpression? = binaryExpression.left
                while (true) {
                    expression = when (expression) {
                        is EReferenceExpression -> {
                            val reference = expression.reference
                            if (reference is EDeclaration && reference.parent is EAbstractContainerComponent)
                                return SvBinaryOperatorKind.ARROW_ASSIGN
                            expression.receiver
                        }
                        is ESvArrayAccessExpression -> expression.array
                        is EConstantPartSelectExpression -> expression.array
                        else -> break
                    }
                }
            } else {
                var expression: EExpression? = binaryExpression.left
                while (true) {
                    expression = when (expression) {
                        is EReferenceExpression -> {
                            val reference = expression.reference
                            if (reference is ETypedElement && reference.type.reference is EClockingBlock)
                                return SvBinaryOperatorKind.ARROW_ASSIGN
                            expression.receiver
                        }
                        is ESvArrayAccessExpression -> expression.array
                        is EConstantPartSelectExpression -> expression.array
                        else -> break
                    }
                }
            }
            return SvBinaryOperatorKind.ASSIGN
        }

        override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
            inAlwaysSeqBlock = true
            super.visitAlwaysSeqBlock(alwaysSeqBlock)
            inAlwaysSeqBlock = false
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            if (binaryExpression.kind == KtBinaryOperatorKind.EQ) {
                val kind = getKind(binaryExpression)
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
