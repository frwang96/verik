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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvForStatement
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ForStatementTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val forStatementTransformerVisitor = ForStatementTransformerVisitor(referenceUpdater)
        projectContext.project.accept(forStatementTransformerVisitor)
        referenceUpdater.flush()
    }

    private class ForStatementTransformerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference == Core.Kt.Collections.F_forEach_Function) {
                val functionLiteral = callExpression.valueArguments[0]
                    .cast<EFunctionLiteralExpression>()
                val valueParameter = functionLiteral.valueParameters[0]
                    .cast<ESvValueParameter>()
                val receiver = callExpression.receiver!!
                val forStatement = when {
                    receiver is ECallExpression && receiver.reference == Core.Kt.Ranges.F_until_Int ->
                        transformForStatementUntil(functionLiteral, valueParameter, receiver)
                    receiver is EReferenceExpression && receiver.type.reference == Core.Jv.Util.C_ArrayList ->
                        transformForStatementArrayList(functionLiteral, valueParameter, receiver)
                    else -> null
                }
                if (forStatement != null) {
                    callExpression.replace(forStatement)
                } else {
                    Messages.INTERNAL_ERROR.on(callExpression, "Unable to transform call expression into for statement")
                }
            }
        }

        private fun transformForStatementUntil(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            callExpression: ECallExpression
        ): ESvForStatement {
            val property = EProperty.named(
                location = valueParameter.location,
                name = valueParameter.name,
                type = valueParameter.type,
                initializer = callExpression.receiver!!,
                isMutable = true
            )
            referenceUpdater.update(valueParameter, property)
            val propertyReferenceExpression = EReferenceExpression(
                property.location,
                property.type.copy(),
                property,
                null
            )
            val condition = EKtBinaryExpression(
                callExpression.location,
                Core.Kt.C_Boolean.toType(),
                propertyReferenceExpression,
                callExpression.valueArguments[0],
                KtBinaryOperatorKind.LT
            )
            val iteration = EKtUnaryExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                ExpressionCopier.deepCopy(propertyReferenceExpression),
                KtUnaryOperatorKind.POST_INC
            )
            return ESvForStatement(
                callExpression.location,
                property,
                condition,
                iteration,
                functionLiteral.body
            )
        }

        private fun transformForStatementArrayList(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            referenceExpression: EReferenceExpression
        ): ESvForStatement {
            val indexProperty = EProperty.temporary(
                location = referenceExpression.location,
                type = Core.Kt.C_Int.toType(),
                initializer = EConstantExpression(referenceExpression.location, Core.Kt.C_Int.toType(), "0"),
                isMutable = true
            )
            val indexReferenceExpression = EReferenceExpression(
                indexProperty.location,
                indexProperty.type.copy(),
                indexProperty,
                null
            )
            val sizeReferenceExpression = EReferenceExpression(
                referenceExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Jv.Util.ArrayList.P_size,
                referenceExpression
            )
            val condition = EKtBinaryExpression(
                referenceExpression.location,
                Core.Kt.C_Boolean.toType(),
                indexReferenceExpression,
                sizeReferenceExpression,
                KtBinaryOperatorKind.LT
            )
            val iteration = EKtUnaryExpression(
                referenceExpression.location,
                Core.Kt.C_Int.toType(),
                ExpressionCopier.deepCopy(indexReferenceExpression),
                KtUnaryOperatorKind.POST_INC
            )
            val elementPropertyInitializer = ECallExpression(
                valueParameter.location,
                valueParameter.type.copy(),
                Core.Jv.Util.ArrayList.F_get_Int,
                ExpressionCopier.deepCopy(referenceExpression),
                arrayListOf(ExpressionCopier.deepCopy(indexReferenceExpression)),
                ArrayList()
            )
            val elementProperty = EProperty.named(
                location = valueParameter.location,
                name = valueParameter.name,
                type = valueParameter.type,
                initializer = elementPropertyInitializer,
                isMutable = false
            )
            referenceUpdater.update(valueParameter, elementProperty)
            val propertyStatement = EPropertyStatement(
                valueParameter.location,
                elementProperty
            )
            val body = functionLiteral.body
            propertyStatement.parent = body
            body.statements.add(0, propertyStatement)
            return ESvForStatement(
                referenceExpression.location,
                indexProperty,
                condition,
                iteration,
                body
            )
        }
    }
}
