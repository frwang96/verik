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
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object UnaryExpressionTransformer : ProjectStage() {

    override val checkNormalization = true

    private val operatorKindMap = HashMap<CoreKtFunctionDeclaration, SvUnaryOperatorKind>()

    init {
        operatorKindMap[Core.Kt.Boolean.NOT] = SvUnaryOperatorKind.EXCL
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(UnaryExpressionVisitor)
    }

    object UnaryExpressionVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = callExpression.reference
            val kind = operatorKindMap[reference]
            if (kind != null) {
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
    }
}
