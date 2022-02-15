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

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedStatement
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object InjectedStatementTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(InjectedStatementTransformerVisitor)
    }

    private object InjectedStatementTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.F_inject_String) {
                val expression = callExpression.valueArguments[0]
                if (expression is EStringTemplateExpression) {
                    val injectedStatement = EInjectedStatement(
                        callExpression.location,
                        expression.entries
                    )
                    callExpression.replace(injectedStatement)
                } else if (expression is ECallExpression && expression.reference == Core.Kt.Text.F_trimIndent) {
                    val receiver = expression.receiver!!
                    if (receiver is EStringTemplateExpression) {
                        val injectedStatement = EInjectedStatement(
                            callExpression.location,
                            StringEntry.trimIndent(receiver.entries)
                        )
                        callExpression.replace(injectedStatement)
                    } else {
                        Messages.ILLEGAL_INJECTED_STATEMENT.on(expression)
                    }
                } else {
                    Messages.ILLEGAL_INJECTED_STATEMENT.on(expression)
                }
            }
        }
    }
}
