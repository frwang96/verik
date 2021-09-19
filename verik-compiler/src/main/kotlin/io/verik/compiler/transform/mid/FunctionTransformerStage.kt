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

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object FunctionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionTransformerVisitor)
    }

    object FunctionTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference is CoreKtFunctionDeclaration) {
                val newReference = reference.transformReference()
                if (newReference != null) {
                    callExpression.reference = newReference
                    return
                }
                val newExpression = reference.transform(callExpression)
                if (newExpression != null)
                    callExpression.replace(newExpression)
            }
        }
    }
}
