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

import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object AssignmentTransformer : MidTransformerStage() {

    override fun process(projectContext: ProjectContext) {
        val assignmentVisitor = AssignmentVisitor()
        projectContext.project.accept(assignmentVisitor)
    }

    class AssignmentVisitor : TreeVisitor() {

        private var inAlwaysSeqBlock = false

        override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
            inAlwaysSeqBlock = true
            super.visitAlwaysSeqBlock(alwaysSeqBlock)
            inAlwaysSeqBlock = false
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            if (binaryExpression.kind == KtBinaryOperatorKind.EQ) {
                val kind = if (inAlwaysSeqBlock) SvBinaryOperatorKind.ARROW_ASSIGN
                else SvBinaryOperatorKind.ASSIGN
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