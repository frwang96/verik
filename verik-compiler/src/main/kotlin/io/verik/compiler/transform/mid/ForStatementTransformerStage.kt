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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.ETemporaryValueParameter
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.sv.EForStatement
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object ForStatementTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val forStatementTransformerVisitor = ForStatementTransformerVisitor(referenceUpdater)
        projectContext.project.accept(forStatementTransformerVisitor)
        referenceUpdater.flush()
    }

    private class ForStatementTransformerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.reference == Core.Kt.Collections.F_forEach_Function) {
                val functionLiteral = callExpression.valueArguments[0]
                    .cast<EFunctionLiteralExpression>()
                    ?: return
                val valueParameter = functionLiteral.valueParameters[0]
                    .cast<ESvValueParameter>()
                    ?: return
                val receiver = callExpression.receiver!!
                val forStatement = when {
                    receiver is EKtCallExpression && receiver.reference == Core.Kt.Ranges.F_until_Int ->
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
            callExpression: EKtCallExpression
        ): EForStatement {
            val initializer = callExpression.receiver!!
            val valueParameterReferenceExpression = EReferenceExpression(
                valueParameter.location,
                valueParameter.type.copy(),
                valueParameter,
                null
            )
            val condition = EKtCallExpression(
                callExpression.location,
                Core.Kt.C_Boolean.toType(),
                Core.Kt.Int.F_lt_Int,
                valueParameterReferenceExpression,
                arrayListOf(callExpression.valueArguments[0]),
                ArrayList()
            )
            val iteration = EKtUnaryExpression(
                callExpression.location,
                Core.Kt.C_Int.toType(),
                ExpressionCopier.copy(valueParameterReferenceExpression),
                KtUnaryOperatorKind.POST_INC
            )
            return EForStatement(
                callExpression.location,
                valueParameter,
                initializer,
                condition,
                iteration,
                functionLiteral.body
            )
        }

        private fun transformForStatementArrayList(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            referenceExpression: EReferenceExpression
        ): EForStatement {
            val forValueParameter = ETemporaryValueParameter(referenceExpression.location, Core.Kt.C_Int.toType())
            val forValueParameterReferenceExpression = EReferenceExpression(
                referenceExpression.location,
                forValueParameter.type.copy(),
                forValueParameter,
                null
            )
            val initializer = EConstantExpression(referenceExpression.location, Core.Kt.C_Int.toType(), "0")
            val sizeReferenceExpression = EReferenceExpression(
                referenceExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Jv.Util.ArrayList.P_size,
                referenceExpression
            )
            val condition = EKtCallExpression(
                referenceExpression.location,
                Core.Kt.C_Boolean.toType(),
                Core.Kt.Int.F_lt_Int,
                forValueParameterReferenceExpression,
                arrayListOf(sizeReferenceExpression),
                ArrayList()
            )
            val iteration = EKtUnaryExpression(
                referenceExpression.location,
                Core.Kt.C_Int.toType(),
                ExpressionCopier.copy(forValueParameterReferenceExpression),
                KtUnaryOperatorKind.POST_INC
            )
            val propertyInitializer = EKtCallExpression(
                valueParameter.location,
                valueParameter.type.copy(),
                Core.Jv.Util.ArrayList.F_get_Int,
                ExpressionCopier.copy(referenceExpression),
                arrayListOf(ExpressionCopier.copy(forValueParameterReferenceExpression)),
                ArrayList()
            )
            val property = ESvProperty(
                valueParameter.location,
                valueParameter.name,
                valueParameter.type,
                propertyInitializer,
                false
            )
            referenceUpdater.update(valueParameter, property)
            val propertyStatement = EPropertyStatement(
                valueParameter.location,
                property
            )
            val body = functionLiteral.body as EKtBlockExpression
            propertyStatement.parent = body
            body.statements.add(0, propertyStatement)
            return EForStatement(
                referenceExpression.location,
                forValueParameter,
                initializer,
                condition,
                iteration,
                body
            )
        }
    }
}
