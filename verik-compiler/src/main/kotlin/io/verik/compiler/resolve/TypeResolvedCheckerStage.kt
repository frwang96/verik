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

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeResolvedCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeResolvedCheckerVisitor)
    }

    private object TypeResolvedCheckerVisitor : TreeVisitor() {

        override fun visitExpression(expression: EExpression) {
            super.visitExpression(expression)
            if (!expression.type.isResolved())
                Messages.EXPRESSION_UNRESOLVED.on(expression)
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.typeArguments.forEach {
                if (!it.isResolved())
                    Messages.TYPE_ARGUMENT_UNRESOLVED.on(callExpression)
            }
        }
    }
}
