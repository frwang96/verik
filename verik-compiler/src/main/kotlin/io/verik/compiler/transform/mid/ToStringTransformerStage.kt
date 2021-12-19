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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.Target

object ToStringTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ToStringTransformerVisitor)
    }

    private object ToStringTransformerVisitor : TreeVisitor() {

        private fun transform(expression: EExpression) {
            if (expression.type.reference is EEnum) {
                if (expression is EReferenceExpression && expression.reference is ESvEnumEntry) {
                    val stringExpression = EStringExpression(
                        expression.location,
                        expression.reference.name
                    )
                    expression.replace(stringExpression)
                } else {
                    val parent = expression.parentNotNull()
                    val callExpression = EKtCallExpression(
                        expression.location,
                        Core.Kt.C_String.toType(),
                        Target.F_name,
                        expression,
                        arrayListOf(),
                        arrayListOf()
                    )
                    parent.replaceChildAsExpressionContainer(expression, callExpression)
                }
            }
        }

        override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            stringTemplateExpression.entries.forEach {
                if (it is ExpressionStringEntry)
                    transform(it.expression)
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            when (callExpression.reference) {
                Core.Kt.Io.F_print_Any -> transform(callExpression.valueArguments[0])
                Core.Kt.Io.F_println_Any -> transform(callExpression.valueArguments[0])
            }
        }
    }
}
