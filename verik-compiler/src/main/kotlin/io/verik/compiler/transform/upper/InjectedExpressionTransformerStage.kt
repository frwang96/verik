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
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object InjectedExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(InjectedExpressionTransformerVisitor)
    }

    private object InjectedExpressionTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference in listOf(Core.Vk.F_inj_String, Core.Vk.F_inji_String)) {
                val expression = callExpression.valueArguments[0]
                if (expression is EStringTemplateExpression) {
                    val injectedExpression = EInjectedExpression(
                        callExpression.location,
                        expression.entries
                    )
                    callExpression.replace(injectedExpression)
                } else if (expression is ECallExpression && expression.reference == Core.Kt.Text.F_trimIndent) {
                    val receiver = expression.receiver!!
                    if (receiver is EStringTemplateExpression) {
                        val injectedExpression = EInjectedExpression(
                            callExpression.location,
                            StringEntry.trimIndent(receiver.entries)
                        )
                        callExpression.replace(injectedExpression)
                    } else {
                        Messages.ILLEGAL_INJECTED_EXPRESSION.on(expression)
                    }
                } else {
                    Messages.ILLEGAL_INJECTED_EXPRESSION.on(expression)
                }
            }
        }
    }
}
