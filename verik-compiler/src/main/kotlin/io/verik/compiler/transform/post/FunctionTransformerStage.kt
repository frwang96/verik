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
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object FunctionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionTransformerVisitor)
    }

    private object FunctionTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            when (val reference = callExpression.reference) {
                is BasicCoreFunctionDeclaration -> {
                    val targetFunctionDeclaration = reference.targetFunctionDeclaration
                    if (targetFunctionDeclaration != null)
                        callExpression.reference = targetFunctionDeclaration
                }
                is TransformableCoreFunctionDeclaration -> {
                    callExpression.replace(reference.transform(callExpression))
                }
                is CoreConstructorDeclaration -> {
                    val targetFunctionDeclaration = reference.targetFunctionDeclaration
                    if (targetFunctionDeclaration != null)
                        callExpression.reference = targetFunctionDeclaration
                }
            }
        }
    }
}
