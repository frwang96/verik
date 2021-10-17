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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.EWhileExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.message.Messages

object ExpressionSpecializer {

    fun <E : EExpression> specializeExpression(expression: E, specializerContext: SpecializerContext): E {
        val copiedExpression = when (expression) {
            is EKtBlockExpression -> specializeKtBlockExpression(expression, specializerContext)
            is EPropertyStatement -> specializePropertyStatement(expression, specializerContext)
            is EKtUnaryExpression -> specializeKtUnaryExpression(expression, specializerContext)
            is EKtBinaryExpression -> specializeKtBinaryExpression(expression, specializerContext)
            is EKtReferenceExpression -> specializeKtReferenceExpression(expression, specializerContext)
            is EKtCallExpression -> specializeKtCallExpression(expression, specializerContext)
            is EConstantExpression -> specializeConstantExpression(expression, specializerContext)
            is EReturnStatement -> specializeReturnStatement(expression, specializerContext)
            is EFunctionLiteralExpression -> specializeFunctionLiteralExpression(expression, specializerContext)
            is EStringTemplateExpression -> specializeStringTemplateExpression(expression, specializerContext)
            is EIfExpression -> specializeIfExpression(expression, specializerContext)
            is EWhenExpression -> specializeWhenExpression(expression, specializerContext)
            is EWhileExpression -> specializeWhileExpression(expression, specializerContext)
            else -> {
                Messages.INTERNAL_ERROR.on(expression, "Unable to specialize expression: $expression")
                expression
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedExpression as E
    }

    private fun specializeKtBlockExpression(
        blockExpression: EKtBlockExpression,
        specializerContext: SpecializerContext
    ): EKtBlockExpression {
        val type = specializerContext.specializeType(blockExpression)
        val statements = blockExpression.statements.map { specializerContext.specialize(it) }
        return EKtBlockExpression(blockExpression.location, type, ArrayList(statements))
    }

    private fun specializePropertyStatement(
        propertyStatement: EPropertyStatement,
        specializerContext: SpecializerContext
    ): EPropertyStatement {
        val property = specializerContext.specialize(propertyStatement.property)
        return EPropertyStatement(propertyStatement.location, property)
    }

    private fun specializeKtUnaryExpression(
        unaryExpression: EKtUnaryExpression,
        specializerContext: SpecializerContext
    ): EKtUnaryExpression {
        val type = specializerContext.specializeType(unaryExpression)
        val expression = specializerContext.specialize(unaryExpression.expression)
        return EKtUnaryExpression(unaryExpression.location, type, expression, unaryExpression.kind)
    }

    private fun specializeKtBinaryExpression(
        binaryExpression: EKtBinaryExpression,
        specializerContext: SpecializerContext
    ): EKtBinaryExpression {
        val type = specializerContext.specializeType(binaryExpression)
        val left = specializerContext.specialize(binaryExpression.left)
        val right = specializerContext.specialize(binaryExpression.right)
        return EKtBinaryExpression(binaryExpression.location, type, left, right, binaryExpression.kind)
    }

    private fun specializeKtReferenceExpression(
        referenceExpression: EKtReferenceExpression,
        specializerContext: SpecializerContext
    ): EKtReferenceExpression {
        val type = specializerContext.specializeType(referenceExpression)
        val receiver = referenceExpression.receiver?.let { specializerContext.specialize(it) }

        val reference = referenceExpression.reference
        return if (reference is EDeclaration) {
            val typeParameterContext = TypeParameterContext.getFromReceiver(
                reference,
                referenceExpression.receiver,
                referenceExpression,
                specializerContext
            )
            val forwardedReference = specializerContext[reference, typeParameterContext, referenceExpression]
            EKtReferenceExpression(referenceExpression.location, type, forwardedReference, receiver)
        } else {
            EKtReferenceExpression(referenceExpression.location, type, reference, receiver)
        }
    }

    private fun specializeKtCallExpression(
        callExpression: EKtCallExpression,
        specializerContext: SpecializerContext
    ): EKtCallExpression {
        val type = specializerContext.specializeType(callExpression)
        val receiver = callExpression.receiver?.let { specializerContext.specialize(it) }
        val valueArguments = callExpression.valueArguments.map { specializerContext.specialize(it) }

        val reference = callExpression.reference
        return if (reference is EKtAbstractFunction) {
            val receiverTypeParameterContext = TypeParameterContext.getFromReceiver(
                reference,
                callExpression.receiver,
                callExpression,
                specializerContext
            )
            val callExpressionTypeParameterContext = TypeParameterContext.getFromTypeArguments(
                callExpression.typeArguments,
                reference,
                callExpression
            )
            val typeParameterContext = TypeParameterContext(
                receiverTypeParameterContext.typeParameterBindings +
                    callExpressionTypeParameterContext.typeParameterBindings
            )
            val forwardedReference = specializerContext[reference, typeParameterContext, callExpression]
            EKtCallExpression(
                callExpression.location,
                type,
                forwardedReference,
                receiver,
                ArrayList(valueArguments),
                arrayListOf()
            )
        } else {
            val typeArguments = callExpression.typeArguments.map {
                specializerContext.specializeType(it, callExpression)
            }
            EKtCallExpression(
                callExpression.location,
                type,
                reference,
                receiver,
                ArrayList(valueArguments),
                ArrayList(typeArguments)
            )
        }
    }

    private fun specializeConstantExpression(
        constantExpression: EConstantExpression,
        specializerContext: SpecializerContext
    ): EConstantExpression {
        val type = specializerContext.specializeType(constantExpression)
        return EConstantExpression(constantExpression.location, type, constantExpression.value)
    }

    private fun specializeReturnStatement(
        returnStatement: EReturnStatement,
        specializerContext: SpecializerContext
    ): EReturnStatement {
        val type = specializerContext.specializeType(returnStatement)
        val expression = returnStatement.expression?.let { specializerContext.specialize(it) }
        return EReturnStatement(returnStatement.location, type, expression)
    }

    private fun specializeFunctionLiteralExpression(
        functionLiteralExpression: EFunctionLiteralExpression,
        specializerContext: SpecializerContext
    ): EFunctionLiteralExpression {
        val valueParameters = functionLiteralExpression.valueParameters.map { specializerContext.specialize(it) }
        val body = specializerContext.specialize(functionLiteralExpression.body)
        return EFunctionLiteralExpression(functionLiteralExpression.location, ArrayList(valueParameters), body)
    }

    private fun specializeStringTemplateExpression(
        stringTemplateExpression: EStringTemplateExpression,
        specializerContext: SpecializerContext
    ): EStringTemplateExpression {
        val entries = stringTemplateExpression.entries.map { entry ->
            when (entry) {
                is LiteralStringEntry -> LiteralStringEntry(entry.text)
                is ExpressionStringEntry -> ExpressionStringEntry(specializerContext.specialize(entry.expression))
            }
        }
        return EStringTemplateExpression(stringTemplateExpression.location, entries)
    }

    private fun specializeIfExpression(
        ifExpression: EIfExpression,
        specializerContext: SpecializerContext
    ): EIfExpression {
        val type = specializerContext.specializeType(ifExpression)
        val condition = specializerContext.specialize(ifExpression.condition)
        val thenExpression = ifExpression.thenExpression?.let { specializerContext.specialize(it) }
        val elseExpression = ifExpression.elseExpression?.let { specializerContext.specialize(it) }
        return EIfExpression(ifExpression.location, type, condition, thenExpression, elseExpression)
    }

    private fun specializeWhenExpression(
        whenExpression: EWhenExpression,
        specializerContext: SpecializerContext
    ): EWhenExpression {
        val type = specializerContext.specializeType(whenExpression)
        val subject = specializerContext.specialize(whenExpression.subject)
        val entries = whenExpression.entries.map { entry ->
            val conditions = entry.conditions.map { specializerContext.specialize(it) }
            val body = specializerContext.specialize(entry.body)
            WhenEntry(ArrayList(conditions), body)
        }
        return EWhenExpression(whenExpression.location, type, subject, entries)
    }

    private fun specializeWhileExpression(
        whileExpression: EWhileExpression,
        specializerContext: SpecializerContext
    ): EWhileExpression {
        val condition = specializerContext.specialize(whileExpression.condition)
        val body = specializerContext.specialize(whileExpression.body)
        return EWhileExpression(whileExpression.location, condition, body, whileExpression.isDoWhile)
    }
}
