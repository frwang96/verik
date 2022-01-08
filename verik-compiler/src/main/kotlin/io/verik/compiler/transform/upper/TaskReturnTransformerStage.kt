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

import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ExpressionExtractor
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TaskReturnTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val expressionExtractor = ExpressionExtractor()
        val taskReturnInternalTransformerVisitor = TaskReturnInternalTransformerVisitor(expressionExtractor)
        projectContext.project.accept(taskReturnInternalTransformerVisitor)
        val taskReturnExternalTransformerVisitor = TaskReturnExternalTransformerVisitor(expressionExtractor)
        projectContext.project.accept(taskReturnExternalTransformerVisitor)
        expressionExtractor.flush()
    }

    private class TaskReturnInternalTransformerVisitor(
        private val expressionExtractor: ExpressionExtractor
    ) : TreeVisitor() {

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
                val newExpression = EReturnStatement(returnStatement.location, Core.Kt.C_Unit.toType(), null)
                val extractedExpression = EKtBinaryExpression(
                    returnStatement.location,
                    Core.Kt.C_Unit.toType(),
                    EReferenceExpression(returnStatement.location, valueParameter.type.copy(), valueParameter, null),
                    expression,
                    KtBinaryOperatorKind.EQ
                )
                expressionExtractor.extract(returnStatement, newExpression, listOf(extractedExpression))
            }
        }
    }

    private class TaskReturnExternalTransformerVisitor(
        private val expressionExtractor: ExpressionExtractor
    ) : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
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

        private fun extract(callExpression: EKtCallExpression, valueParameter: ESvValueParameter) {
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
            val callExpressionReplacement = EKtCallExpression(
                callExpression.location,
                callExpression.type,
                callExpression.reference,
                callExpression.receiver,
                callExpression.valueArguments,
                callExpression.typeArguments
            )
            referenceExpression.parent = callExpressionReplacement
            callExpressionReplacement.valueArguments.add(referenceExpression)
            expressionExtractor.extract(
                callExpression,
                ExpressionCopier.deepCopy(referenceExpression),
                listOf(propertyStatement, callExpressionReplacement)
            )
        }
    }
}
