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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object UnaryExpressionReducer : ProjectPass {

    private val referenceMap = HashMap<ReducerEntry, CoreKtFunctionDeclaration>()

    init {
        referenceMap[ReducerEntry(Core.Kt.BOOLEAN, KtUnaryOperatorKind.EXCL)] = Core.Kt.Boolean.NOT
    }

    override fun pass(projectContext: ProjectContext) {
        projectContext.project.accept(UnaryExpressionVisitor)
    }

    data class ReducerEntry(
        val expressionDeclaration: CoreClassDeclaration,
        val kind: KtUnaryOperatorKind
    )

    object UnaryExpressionVisitor : TreeVisitor() {

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            val expressionDeclaration = unaryExpression.expression.type.reference
            val kind = unaryExpression.kind
            if (expressionDeclaration is CoreClassDeclaration) {
                val reference = referenceMap[ReducerEntry(expressionDeclaration, kind)]
                if (reference != null) {
                    unaryExpression.replace(
                        ECallExpression(
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
            m.error("Unary expression could not be reduced", unaryExpression)
        }
    }
}