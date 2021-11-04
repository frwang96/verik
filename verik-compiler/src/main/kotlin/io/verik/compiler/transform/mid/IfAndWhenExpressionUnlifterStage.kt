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
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext

object IfAndWhenExpressionUnlifterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val subexpressionExtractor = SubexpressionExtractor()
        val ifAndWhenExpressionUnlifterVisitor = IfAndWhenExpressionUnlifterVisitor(subexpressionExtractor)
        projectContext.project.accept(ifAndWhenExpressionUnlifterVisitor)
        subexpressionExtractor.flush()
    }

    private class IfAndWhenExpressionUnlifterVisitor(
        private val subexpressionExtractor: SubexpressionExtractor
    ) : TreeVisitor() {

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            if (ifExpression.getExpressionType().isSubexpression()) {
                val temporaryProperty = ETemporaryProperty(ifExpression.location)
                temporaryProperty.init(ifExpression.type.copy(), null)
                val referenceExpression = EReferenceExpression(
                    ifExpression.location,
                    temporaryProperty.type.copy(),
                    temporaryProperty,
                    null
                )
                val propertyStatement = EPropertyStatement(
                    ifExpression.location,
                    temporaryProperty
                )
                val ifExpressionReplacement = getIfExpressionReplacement(ifExpression, temporaryProperty)
                subexpressionExtractor.extract(
                    ifExpression,
                    referenceExpression,
                    listOf(propertyStatement, ifExpressionReplacement)
                )
            }
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            if (whenExpression.getExpressionType().isSubexpression()) {
                val temporaryProperty = ETemporaryProperty(whenExpression.location)
                temporaryProperty.init(whenExpression.type.copy(), null)
                val referenceExpression = EReferenceExpression(
                    whenExpression.location,
                    temporaryProperty.type.copy(),
                    temporaryProperty,
                    null
                )
                val propertyStatement = EPropertyStatement(
                    whenExpression.location,
                    temporaryProperty
                )
                val whenExpressionReplacement = getWhenExpressionReplacement(whenExpression, temporaryProperty)
                subexpressionExtractor.extract(
                    whenExpression,
                    referenceExpression,
                    listOf(propertyStatement, whenExpressionReplacement)
                )
            }
        }

        private fun getWhenExpressionReplacement(
            whenExpression: EWhenExpression,
            temporaryProperty: ETemporaryProperty
        ): EWhenExpression {
            whenExpression.entries.forEach {
                it.body = wrapAssignment(it.body, temporaryProperty)
                it.body.parent = whenExpression
            }
            return EWhenExpression(
                whenExpression.location,
                Core.Kt.C_Unit.toType(),
                whenExpression.subject,
                whenExpression.entries
            )
        }

        private fun getIfExpressionReplacement(
            ifExpression: EIfExpression,
            temporaryProperty: ETemporaryProperty
        ): EIfExpression {
            val thenExpression = wrapAssignment(ifExpression.thenExpression!!, temporaryProperty)
            val elseExpression = wrapAssignment(ifExpression.elseExpression!!, temporaryProperty)
            return EIfExpression(
                ifExpression.location,
                Core.Kt.C_Unit.toType(),
                ifExpression.condition,
                thenExpression,
                elseExpression
            )
        }

        private fun wrapAssignment(expression: EExpression, temporaryProperty: ETemporaryProperty): EExpression {
            return if (expression is EKtBlockExpression) {
                val index = expression.statements.lastIndex
                expression.statements[index] = wrapAssignment(expression.statements[index], temporaryProperty)
                expression.statements[index].parent = expression
                expression
            } else {
                EKtBinaryExpression(
                    expression.location,
                    Core.Kt.C_Unit.toType(),
                    EReferenceExpression(expression.location, temporaryProperty.type.copy(), temporaryProperty, null),
                    expression,
                    KtBinaryOperatorKind.EQ
                )
            }
        }
    }
}
