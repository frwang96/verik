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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TaskReturnTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TaskReturnInternalTransformerVisitor)
        projectContext.project.accept(TaskReturnExternalTransformerVisitor)
    }

    private object TaskReturnInternalTransformerVisitor : TreeVisitor() {

        private var returnValueParameter: ESvValueParameter? = null

        override fun visitTask(task: ETask) {
            if (task.valueParameters.isNotEmpty()) {
                val valueParameter = task.valueParameters.last()
                if (!valueParameter.isInput) {
                    returnValueParameter = valueParameter
                    super.visitTask(task)
                    returnValueParameter = null
                }
            }
        }

        override fun visitReturnStatement(returnStatement: EReturnStatement) {
            super.visitReturnStatement(returnStatement)
            val valueParameter = returnValueParameter
            val expression = returnStatement.expression
            if (valueParameter != null && expression != null) {
                val newReturnStatement = EReturnStatement(returnStatement.location, Core.Kt.C_Unit.toType(), null)
                val binaryExpression = EKtBinaryExpression(
                    returnStatement.location,
                    Core.Kt.C_Unit.toType(),
                    EReferenceExpression(returnStatement.location, valueParameter.type.copy(), valueParameter, null),
                    expression,
                    KtBinaryOperatorKind.EQ
                )
                EBlockExpression.extract(returnStatement, listOf(binaryExpression, newReturnStatement))
            }
        }
    }

    private object TaskReturnExternalTransformerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val task = callExpression.reference
            if (task is ETask && callExpression.type.reference != Core.Kt.C_Unit) {
                if (task.valueParameters.isNotEmpty()) {
                    val valueParameter = task.valueParameters.last()
                    if (!valueParameter.isInput) {
                        extract(callExpression, valueParameter)
                        return
                    }
                }
                Messages.INTERNAL_ERROR.on(callExpression, "Could not find task return value parameter")
            }
        }

        private fun extract(callExpression: ECallExpression, valueParameter: ESvValueParameter) {
            val property = ESvProperty.getTemporary(
                callExpression.location,
                valueParameter.type.copy(),
                initializer = null,
                isMutable = true
            )
            val propertyStatement = EPropertyStatement(callExpression.location, property)
            val referenceExpression = EReferenceExpression(
                callExpression.location,
                property.type.copy(),
                property,
                null
            )
            val newCallExpression = ExpressionCopier.shallowCopy(callExpression)
            referenceExpression.parent = newCallExpression
            newCallExpression.valueArguments.add(referenceExpression)
            val extractedExpressions = listOf(
                propertyStatement,
                newCallExpression,
                ExpressionCopier.deepCopy(referenceExpression)
            )
            EBlockExpression.extract(callExpression, extractedExpressions)
        }
    }
}
