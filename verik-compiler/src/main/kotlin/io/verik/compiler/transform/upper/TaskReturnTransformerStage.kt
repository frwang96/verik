/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.ValueParameterKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that transforms the return value of a task to an output value parameter.
 */
object TaskReturnTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val taskReturnInternalTransformerVisitor = TaskReturnInternalTransformerVisitor()
        projectContext.project.accept(taskReturnInternalTransformerVisitor)
        projectContext.project.accept(TaskReturnExternalTransformerVisitor)
    }

    private class TaskReturnInternalTransformerVisitor : TreeVisitor() {

        private var returnValueParameter: ESvValueParameter? = null

        override fun visitTask(task: ETask) {
            if (task.valueParameters.isNotEmpty()) {
                val valueParameter = task.valueParameters.last()
                if (valueParameter.kind == ValueParameterKind.OUTPUT) {
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
                val referenceExpression = EReferenceExpression(
                    returnStatement.location,
                    valueParameter.type.copy(),
                    valueParameter,
                    null,
                    false
                )
                val binaryExpression = EKtBinaryExpression(
                    returnStatement.location,
                    Core.Kt.C_Unit.toType(),
                    referenceExpression,
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
                    if (valueParameter.kind == ValueParameterKind.OUTPUT) {
                        extract(callExpression, valueParameter)
                        return
                    }
                }
                Messages.INTERNAL_ERROR.on(callExpression, "Could not find task return value parameter")
            }
        }

        private fun extract(callExpression: ECallExpression, valueParameter: ESvValueParameter) {
            val property = EProperty.temporary(
                callExpression.location,
                valueParameter.type.copy(),
                initializer = null,
                isMutable = true
            )
            val propertyStatement = EPropertyStatement(callExpression.location, property)
            val referenceExpression = EReferenceExpression.of(property.location, property)
            val newCallExpression = ExpressionCopier.shallowCopy(callExpression)
            referenceExpression.parent = newCallExpression
            newCallExpression.valueArguments.add(referenceExpression)

            val extractedExpressions = arrayListOf(propertyStatement, newCallExpression)
            if (callExpression.parent !is EBlockExpression) {
                extractedExpressions.add(ExpressionCopier.deepCopy(referenceExpression))
            }
            EBlockExpression.extract(callExpression, extractedExpressions)
        }
    }
}
