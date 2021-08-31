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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object AssignmentOperatorReducerStage : ProjectStage() {

    override val checkNormalization = true

    private val assignmentOperatorMap = HashMap<KtBinaryOperatorKind, KtBinaryOperatorKind>()

    init {
        assignmentOperatorMap[KtBinaryOperatorKind.PLUS_EQ] = KtBinaryOperatorKind.PLUS
        assignmentOperatorMap[KtBinaryOperatorKind.MINUS_EQ] = KtBinaryOperatorKind.MINUS
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(AssignmentOperatorReducerVisitor)
    }

    object AssignmentOperatorReducerVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val kind = assignmentOperatorMap[binaryExpression.kind]
            if (kind != null) {
                val copyExpression = binaryExpression.left.copy()
                val assignmentExpression = EKtBinaryExpression(
                    binaryExpression.location,
                    binaryExpression.type,
                    binaryExpression.left,
                    EKtBinaryExpression(
                        binaryExpression.location,
                        copyExpression.type.copy(),
                        copyExpression,
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
