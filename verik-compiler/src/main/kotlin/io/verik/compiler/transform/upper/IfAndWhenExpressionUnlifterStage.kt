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

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.element.sv.ESvProperty
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
            if (ifExpression.getExpressionType().isSubexpression()) {
                val property = ESvProperty.getTemporary(
                    location = ifExpression.location,
                    type = ifExpression.type.copy(),
                    initializer = null,
                    isMutable = false
                )
                val propertyStatement = EPropertyStatement(ifExpression.location, property)
                val newIfExpression = getIfExpressionReplacement(ifExpression, property)
                val referenceExpression = EReferenceExpression(
                    ifExpression.location,
                    property.type.copy(),
                    property,
                    null
                )
                val extractedExpressions = listOf(
                    propertyStatement,
                    newIfExpression,
                    referenceExpression
                )
                EKtBlockExpression.extract(ifExpression, extractedExpressions)
            }
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            if (whenExpression.getExpressionType().isSubexpression()) {
                val property = ESvProperty.getTemporary(
                    location = whenExpression.location,
                    type = whenExpression.type.copy(),
                    initializer = null,
                    isMutable = false
                )
                val referenceExpression = EReferenceExpression(
                    whenExpression.location,
                    property.type.copy(),
                    property,
                    null
                )
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
                EKtBlockExpression.extract(whenExpression, extractedExpressions)
            }
        }

        private fun getWhenExpressionReplacement(
            whenExpression: EWhenExpression,
            property: ESvProperty
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
            property: ESvProperty
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

        private fun wrapAssignmentBlock(
            expression: EAbstractBlockExpression,
            property: ESvProperty
        ): EAbstractBlockExpression {
            val index = expression.statements.lastIndex
            val wrappedExpression = wrapAssignmentExpression(expression.statements[index], property)
            wrappedExpression.parent = expression
            expression.statements[index] = wrappedExpression
            expression.type = wrappedExpression.type.copy()
            return expression
        }

        private fun wrapAssignmentExpression(expression: EExpression, property: ESvProperty): EExpression {
            return if (expression.type.reference == Core.Kt.C_Nothing) {
                expression
            } else {
                EKtBinaryExpression(
                    expression.location,
                    Core.Kt.C_Unit.toType(),
                    EReferenceExpression(expression.location, property.type.copy(), property, null),
                    expression,
                    KtBinaryOperatorKind.EQ
                )
            }
        }
    }
}
