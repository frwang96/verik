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

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object UnaryExpressionReducerStage : ProjectStage() {

    override val checkNormalization = true

    private val referenceMap = HashMap<ReducerEntry, CoreKtFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.BOOLEAN, KtUnaryOperatorKind.EXCL)] = Core.Kt.Boolean.NOT
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UnaryExpressionReducerVisitor)
    }

    data class ReducerEntry(
        val expressionDeclaration: CoreClassDeclaration,
        val kind: KtUnaryOperatorKind
    )

    object UnaryExpressionReducerVisitor : TreeVisitor() {

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            val expressionDeclaration = unaryExpression.expression.type.reference
            val kind = unaryExpression.kind
            if (expressionDeclaration is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(expressionDeclaration, kind)]
                if (reference != null) {
                    unaryExpression.replace(
                        EKtCallExpression(
                            unaryExpression.location,
                            unaryExpression.type,
                            reference,
                            unaryExpression.expression,
                            arrayListOf(),
                            arrayListOf()
                        )
                    )
                    return
                }
            }
            Messages.INTERNAL_ERROR.on(unaryExpression, "Unary expression could not be reduced")
        }
    }
}
