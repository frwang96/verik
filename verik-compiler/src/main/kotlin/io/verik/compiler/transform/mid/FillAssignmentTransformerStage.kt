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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext

object FillAssignmentTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FillAssignmentTransformerVisitor)
    }

    private object FillAssignmentTransformerVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val right = binaryExpression.right
            if (binaryExpression.kind == KtBinaryOperatorKind.EQ && right is EKtCallExpression) {
                when (right.reference) {
                    Core.Vk.Ubit.F_fill_Int_Boolean -> {
                        if (expressionMatches(binaryExpression.left, right.receiver!!)) {
                            val arrayAccessExpression = ESvArrayAccessExpression(
                                binaryExpression.location,
                                right.valueArguments[1].type.copy(),
                                binaryExpression.left,
                                right.valueArguments[0]
                            )
                            val newBinaryExpression = EKtBinaryExpression(
                                binaryExpression.location,
                                Core.Kt.C_Unit.toType(),
                                arrayAccessExpression,
                                right.valueArguments[1],
                                KtBinaryOperatorKind.EQ
                            )
                            binaryExpression.replace(newBinaryExpression)
                        }
                    }
                    Core.Vk.Ubit.F_fill_Int_Ubit -> {
                        if (expressionMatches(binaryExpression.left, right.receiver!!)) {
                            val width = right.valueArguments[1].type.asBitWidth(right)
                            val constantExpression = EConstantExpression(
                                right.location,
                                Core.Kt.C_Int.toType(),
                                "${width - 1}"
                            )
                            val msbIndex = EKtCallExpression(
                                right.location,
                                Core.Kt.C_Int.toType(),
                                Core.Kt.Int.F_plus_Int,
                                ExpressionCopier.copy(right.valueArguments[0]),
                                arrayListOf(constantExpression),
                                arrayListOf()
                            )
                            val constantPartSelectExpression = EConstantPartSelectExpression(
                                binaryExpression.location,
                                right.valueArguments[1].type.copy(),
                                binaryExpression.left,
                                msbIndex,
                                right.valueArguments[0]
                            )
                            val newBinaryExpression = EKtBinaryExpression(
                                binaryExpression.location,
                                Core.Kt.C_Unit.toType(),
                                constantPartSelectExpression,
                                right.valueArguments[1],
                                KtBinaryOperatorKind.EQ
                            )
                            binaryExpression.replace(newBinaryExpression)
                        }
                    }
                }
            }
        }

        private fun expressionMatches(left: EExpression?, right: EExpression?): Boolean {
            return when {
                left == null && right == null -> true
                left is EReferenceExpression && right is EReferenceExpression -> {
                    if (left.reference != right.reference) false
                    else expressionMatches(left.receiver, right.receiver)
                }
                else -> false
            }
        }
    }
}
