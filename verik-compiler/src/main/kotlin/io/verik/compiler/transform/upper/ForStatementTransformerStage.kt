/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
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
import io.verik.compiler.constant.ConstantBuilder
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
                    receiver is ECallExpression && receiver.reference == Core.Kt.Int.F_rangeTo_Int ->
                        transformForStatementRangeTo(functionLiteral, valueParameter, receiver)
                    receiver is ECallExpression && receiver.reference == Core.Kt.Ranges.F_until_Int ->
                        transformForStatementUntil(functionLiteral, valueParameter, receiver)
                    receiver is EReferenceExpression && receiver.type.reference == Core.Vk.C_Queue ->
                        transformForStatementQueue(functionLiteral, valueParameter, receiver)
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

        private fun transformForStatementRangeTo(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            callExpression: ECallExpression
        ): ESvForStatement {
            return transformForStatement(
                functionLiteral,
                valueParameter,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                true
            ) {
                EReferenceExpression.of(it.location, it)
            }
        }

        private fun transformForStatementUntil(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            callExpression: ECallExpression
        ): ESvForStatement {
            return transformForStatement(
                functionLiteral,
                valueParameter,
                callExpression.receiver!!,
                callExpression.valueArguments[0],
                false
            ) {
                EReferenceExpression.of(it.location, it)
            }
        }

        private fun transformForStatementQueue(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            referenceExpression: EReferenceExpression
        ): ESvForStatement {
            val startExpression = ConstantBuilder.buildInt(referenceExpression.location, 0)
            val endExpression = EReferenceExpression(
                referenceExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Vk.Queue.P_size,
                referenceExpression,
                false
            )
            return transformForStatement(
                functionLiteral,
                valueParameter,
                startExpression,
                endExpression,
                false
            ) {
                ECallExpression(
                    valueParameter.location,
                    valueParameter.type.copy(),
                    Core.Vk.Queue.F_get_Int,
                    ExpressionCopier.deepCopy(referenceExpression),
                    false,
                    arrayListOf(EReferenceExpression.of(valueParameter.location, it)),
                    ArrayList()
                )
            }
        }

        private fun transformForStatementArrayList(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            referenceExpression: EReferenceExpression
        ): ESvForStatement {
            val startExpression = ConstantBuilder.buildInt(referenceExpression.location, 0)
            val endExpression = EReferenceExpression(
                referenceExpression.location,
                Core.Kt.C_Int.toType(),
                Core.Jv.Util.ArrayList.P_size,
                referenceExpression,
                false
            )
            return transformForStatement(
                functionLiteral,
                valueParameter,
                startExpression,
                endExpression,
                false
            ) {
                ECallExpression(
                    valueParameter.location,
                    valueParameter.type.copy(),
                    Core.Jv.Util.ArrayList.F_get_Int,
                    ExpressionCopier.deepCopy(referenceExpression),
                    false,
                    arrayListOf(EReferenceExpression.of(valueParameter.location, it)),
                    ArrayList()
                )
            }
        }

        private fun transformForStatement(
            functionLiteral: EFunctionLiteralExpression,
            valueParameter: ESvValueParameter,
            startExpression: EExpression,
            endExpression: EExpression,
            isEndInclusive: Boolean,
            elementExpressionBuilder: (EProperty) -> EExpression
        ): ESvForStatement {
            val indexProperty = EProperty.temporary(
                startExpression.location,
                Core.Kt.C_Int.toType(),
                startExpression,
                true
            )
            val indexReferenceExpression = EReferenceExpression.of(indexProperty.location, indexProperty)
            val condition = EKtBinaryExpression(
                startExpression.location,
                Core.Kt.C_Boolean.toType(),
                indexReferenceExpression,
                endExpression,
                if (isEndInclusive) KtBinaryOperatorKind.LTEQ else KtBinaryOperatorKind.LT
            )
            val iteration = EKtUnaryExpression(
                startExpression.location,
                Core.Kt.C_Int.toType(),
                ExpressionCopier.deepCopy(indexReferenceExpression),
                KtUnaryOperatorKind.POST_INC
            )
            val elementProperty = EProperty.named(
                location = valueParameter.location,
                name = valueParameter.name,
                type = valueParameter.type,
                initializer = elementExpressionBuilder(indexProperty),
                isMutable = false
            )
            referenceUpdater.update(valueParameter, elementProperty)
            val propertyStatement = EPropertyStatement(elementProperty.location, elementProperty)
            val body = functionLiteral.body
            propertyStatement.parent = body
            body.statements.add(0, propertyStatement)
            return ESvForStatement(
                startExpression.location,
                indexProperty,
                condition,
                iteration,
                body
            )
        }
    }
}
