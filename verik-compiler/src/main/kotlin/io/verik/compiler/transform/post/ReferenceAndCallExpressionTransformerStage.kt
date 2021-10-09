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

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object ReferenceAndCallExpressionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ReferenceAndCallExpressionTransformerVisitor)
    }

    private object ReferenceAndCallExpressionTransformerVisitor : TreeVisitor() {

        private fun getIsScopeResolution(receiver: EExpression?): Boolean {
            return receiver is ESvReferenceExpression &&
                (receiver.reference is EBasicPackage || receiver.reference is ESvBasicClass)
        }

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            referenceExpression.replace(
                ESvReferenceExpression(
                    referenceExpression.location,
                    referenceExpression.type,
                    referenceExpression.reference,
                    referenceExpression.receiver,
                    getIsScopeResolution(referenceExpression.receiver)
                )
            )
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.replace(
                ESvCallExpression(
                    callExpression.location,
                    callExpression.type,
                    callExpression.reference,
                    callExpression.receiver,
                    callExpression.valueArguments,
                    getIsScopeResolution(callExpression.receiver)
                )
            )
        }
    }
}
