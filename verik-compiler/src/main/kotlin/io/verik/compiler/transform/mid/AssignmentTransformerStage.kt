/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object AssignmentTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val assignmentTransformerVisitor = AssignmentTransformerVisitor()
        projectContext.project.accept(assignmentTransformerVisitor)
    }

    private class AssignmentTransformerVisitor : TreeVisitor() {

        private var inAlwaysSeqBlock = false

        private fun getKind(binaryExpression: EKtBinaryExpression): SvBinaryOperatorKind {
            if (inAlwaysSeqBlock) {
                var referenceExpression: EExpression? = binaryExpression.left
                while (referenceExpression is EReferenceExpression) {
                    val reference = referenceExpression.reference
                    if (reference is EDeclaration && reference.parent is EAbstractContainerComponent)
                        return SvBinaryOperatorKind.ARROW_ASSIGN
                    referenceExpression = referenceExpression.receiver
                }
            } else {
                var referenceExpression: EExpression? = binaryExpression.left
                while (referenceExpression is EReferenceExpression) {
                    val reference = referenceExpression.reference
                    if (reference is ETypedElement && reference.type.reference is EClockingBlock)
                        return SvBinaryOperatorKind.ARROW_ASSIGN
                    referenceExpression = referenceExpression.receiver
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
