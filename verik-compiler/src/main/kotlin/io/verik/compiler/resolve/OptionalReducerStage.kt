/*
 * Copyright (c) 2022 Francis Wang
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

import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object OptionalReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(OptionalReducerVisitor)
    }

    private object OptionalReducerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.F_optional_Function) {
                val typeArgument = callExpression.typeArguments[0]
                when (typeArgument.reference) {
                    Cardinal.TRUE -> {
                        val functionLiteralExpression = callExpression
                            .valueArguments[0]
                            .cast<EFunctionLiteralExpression>()
                        if (functionLiteralExpression.body.statements.size == 1) {
                            callExpression.replace(functionLiteralExpression.body.statements[0])
                            return
                        }
                    }
                    Cardinal.FALSE -> {
                        val nullExpression = ENullExpression(callExpression.location)
                        callExpression.replace(nullExpression)
                        return
                    }
                }
                if (typeArgument.isCardinalType()) {
                    Messages.CARDINAL_NOT_BOOLEAN.on(callExpression, typeArgument)
                } else {
                    Messages.INTERNAL_ERROR.on(callExpression, "Unable to reduce optional function")
                }
            }
        }
    }
}
