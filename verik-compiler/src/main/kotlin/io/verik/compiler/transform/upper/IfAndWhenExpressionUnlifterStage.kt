/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object IfAndWhenExpressionUnlifterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(IfAndWhenExpressionUnlifterVisitor)
    }

    private object IfAndWhenExpressionUnlifterVisitor : TreeVisitor() {

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            if (ifExpression.getExpressionKind().isSubexpression()) {
                val property = EProperty.temporary(
                    location = ifExpression.location,
                    type = ifExpression.type.copy(),
                    initializer = null,
                    isMutable = false
                )
                val propertyStatement = EPropertyStatement(ifExpression.location, property)
                val newIfExpression = getIfExpressionReplacement(ifExpression, property)
                val referenceExpression = EReferenceExpression.of(property.location, property)
                val extractedExpressions = listOf(
                    propertyStatement,
                    newIfExpression,
                    referenceExpression
                )
                EBlockExpression.extract(ifExpression, extractedExpressions)
            }
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            if (whenExpression.getExpressionKind().isSubexpression()) {
                val property = EProperty.temporary(
                    location = whenExpression.location,
                    type = whenExpression.type.copy(),
                    initializer = null,
                    isMutable = false
                )
                val referenceExpression = EReferenceExpression.of(property.location, property)
                val propertyStatement = EPropertyStatement(
                    whenExpression.location,
                    property
                )
                val newWhenExpression = getWhenExpressionReplacement(whenExpression, property)
                val extractedExpressions = listOf(
                    propertyStatement,
                    newWhenExpression,
                    referenceExpression
                )
                EBlockExpression.extract(whenExpression, extractedExpressions)
            }
        }

        private fun getWhenExpressionReplacement(
            whenExpression: EWhenExpression,
            property: EProperty
        ): EWhenExpression {
            whenExpression.entries.forEach {
                it.body = wrapAssignmentBlock(it.body, property)
                it.body.parent = whenExpression
            }
            return EWhenExpression(
                whenExpression.location,
                whenExpression.endLocation,
                Core.Kt.C_Unit.toType(),
                whenExpression.subject,
                whenExpression.entries
            )
        }

        private fun getIfExpressionReplacement(
            ifExpression: EIfExpression,
            property: EProperty
        ): EIfExpression {
            val thenExpression = wrapAssignmentBlock(ifExpression.thenExpression!!, property)
            val elseExpression = wrapAssignmentBlock(ifExpression.elseExpression!!, property)
            return EIfExpression(
                ifExpression.location,
                Core.Kt.C_Unit.toType(),
                ifExpression.condition,
                thenExpression,
                elseExpression
            )
        }

        private fun wrapAssignmentBlock(expression: EBlockExpression, property: EProperty): EBlockExpression {
            val index = expression.statements.lastIndex
            val wrappedExpression = wrapAssignmentExpression(expression.statements[index], property)
            wrappedExpression.parent = expression
            expression.statements[index] = wrappedExpression
            expression.type = wrappedExpression.type.copy()
            return expression
        }

        private fun wrapAssignmentExpression(expression: EExpression, property: EProperty): EExpression {
            return if (expression.type.reference == Core.Kt.C_Nothing) {
                expression
            } else {
                EKtBinaryExpression(
                    expression.location,
                    Core.Kt.C_Unit.toType(),
                    EReferenceExpression.of(expression.location, property),
                    expression,
                    KtBinaryOperatorKind.EQ
                )
            }
        }
    }
}
