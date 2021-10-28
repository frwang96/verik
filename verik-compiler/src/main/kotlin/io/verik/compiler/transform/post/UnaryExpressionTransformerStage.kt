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

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.UnaryCoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object UnaryExpressionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UnaryExpressionTransformerVisitor)
    }

    private object UnaryExpressionTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is UnaryCoreFunctionDeclaration) {
                val kind = reference.getOperatorKind()
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
