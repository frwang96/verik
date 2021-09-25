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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreKtBinaryFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object BinaryExpressionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BinaryExpressionTransformerVisitor)
    }

    object BinaryExpressionTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is CoreKtBinaryFunctionDeclaration) {
                val kind = reference.getOperatorKind()
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
