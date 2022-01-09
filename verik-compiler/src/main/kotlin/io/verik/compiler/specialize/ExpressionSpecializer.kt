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
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.kt.EAsExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.message.Messages

object ExpressionSpecializer {

    fun <E : EExpression> specializeExpression(expression: E, specializeContext: SpecializeContext): E {
        val copiedExpression = when (expression) {
            is EKtBlockExpression -> specializeKtBlockExpression(expression, specializeContext)
            is EPropertyStatement -> specializePropertyStatement(expression, specializeContext)
            is EKtUnaryExpression -> specializeKtUnaryExpression(expression, specializeContext)
            is EKtBinaryExpression -> specializeKtBinaryExpression(expression, specializeContext)
            is EReferenceExpression -> specializeReferenceExpression(expression, specializeContext)
            is EKtCallExpression -> specializeKtCallExpression(expression, specializeContext)
            is EConstantExpression -> specializeConstantExpression(expression, specializeContext)
            is EThisExpression -> specializeThisExpression(expression, specializeContext)
            is ESuperExpression -> specializeSuperExpression(expression, specializeContext)
            is EReturnStatement -> specializeReturnStatement(expression, specializeContext)
            is EFunctionLiteralExpression -> specializeFunctionLiteralExpression(expression, specializeContext)
            is EStringTemplateExpression -> specializeStringTemplateExpression(expression, specializeContext)
            is EIsExpression -> specializeIsExpression(expression, specializeContext)
            is EAsExpression -> specializeAsExpression(expression, specializeContext)
            is EIfExpression -> specializeIfExpression(expression, specializeContext)
            is EWhenExpression -> specializeWhenExpression(expression, specializeContext)
            is EWhileStatement -> specializeWhileStatement(expression, specializeContext)
            else -> Messages.INTERNAL_ERROR.on(expression, "Unable to specialize expression: $expression")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedExpression as E
    }

    private fun specializeKtBlockExpression(
        blockExpression: EKtBlockExpression,
        specializeContext: SpecializeContext
    ): EKtBlockExpression {
        val type = specializeContext.specializeType(blockExpression)
        val statements = blockExpression.statements.map { specializeContext.specialize(it) }
        return EKtBlockExpression(
            blockExpression.location,
            blockExpression.endLocation,
            type,
            ArrayList(statements)
        )
    }

    private fun specializePropertyStatement(
        propertyStatement: EPropertyStatement,
        specializeContext: SpecializeContext
    ): EPropertyStatement {
        val property = specializeContext.specialize(propertyStatement.property)
        return EPropertyStatement(propertyStatement.location, property)
    }

    private fun specializeKtUnaryExpression(
        unaryExpression: EKtUnaryExpression,
        specializeContext: SpecializeContext
    ): EKtUnaryExpression {
        val type = specializeContext.specializeType(unaryExpression)
        val expression = specializeContext.specialize(unaryExpression.expression)
        return EKtUnaryExpression(unaryExpression.location, type, expression, unaryExpression.kind)
    }

    private fun specializeKtBinaryExpression(
        binaryExpression: EKtBinaryExpression,
        specializeContext: SpecializeContext
    ): EKtBinaryExpression {
        val type = specializeContext.specializeType(binaryExpression)
        val left = specializeContext.specialize(binaryExpression.left)
        val right = specializeContext.specialize(binaryExpression.right)
        return EKtBinaryExpression(binaryExpression.location, type, left, right, binaryExpression.kind)
    }

    private fun specializeReferenceExpression(
        referenceExpression: EReferenceExpression,
        specializeContext: SpecializeContext
    ): EReferenceExpression {
        val type = specializeContext.specializeType(referenceExpression)
        val receiver = referenceExpression.receiver?.let { specializeContext.specialize(it) }

        val reference = referenceExpression.reference
        return if (reference is EDeclaration) {
            val typeParameterContext = TypeParameterContext.getFromReceiver(referenceExpression, specializeContext)
            val forwardedReference = specializeContext[reference, typeParameterContext, referenceExpression]
            EReferenceExpression(referenceExpression.location, type, forwardedReference, receiver)
        } else {
            EReferenceExpression(referenceExpression.location, type, reference, receiver)
        }
    }

    private fun specializeKtCallExpression(
        callExpression: EKtCallExpression,
        specializeContext: SpecializeContext
    ): EKtCallExpression {
        val type = specializeContext.specializeType(callExpression)
        val receiver = callExpression.receiver?.let { specializeContext.specialize(it) }
        val valueArguments = callExpression.valueArguments.map { specializeContext.specialize(it) }

        val reference = callExpression.reference
        return if (reference is EKtAbstractFunction) {
            val receiverTypeParameterContext = TypeParameterContext.getFromReceiver(
                callExpression,
                specializeContext
            )
            val callExpressionTypeParameterContext = TypeParameterContext.getFromTypeArguments(
                callExpression.typeArguments,
                reference,
                specializeContext,
                callExpression
            )
            val typeParameterContext = TypeParameterContext(
                receiverTypeParameterContext.typeParameterBindings +
                    callExpressionTypeParameterContext.typeParameterBindings
            )
            val forwardedReference = specializeContext[reference, typeParameterContext, callExpression]
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
                specializeContext.specializeType(it, callExpression)
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
        specializeContext: SpecializeContext
    ): EConstantExpression {
        val type = specializeContext.specializeType(constantExpression)
        return EConstantExpression(constantExpression.location, type, constantExpression.value)
    }

    private fun specializeThisExpression(
        thisExpression: EThisExpression,
        specializeContext: SpecializeContext
    ): EThisExpression {
        val type = specializeContext.specializeType(thisExpression)
        return EThisExpression(thisExpression.location, type)
    }

    private fun specializeSuperExpression(
        superExpression: ESuperExpression,
        specializeContext: SpecializeContext
    ): ESuperExpression {
        val type = specializeContext.specializeType(superExpression)
        return ESuperExpression(superExpression.location, type)
    }

    private fun specializeReturnStatement(
        returnStatement: EReturnStatement,
        specializeContext: SpecializeContext
    ): EReturnStatement {
        val type = specializeContext.specializeType(returnStatement)
        val expression = returnStatement.expression?.let { specializeContext.specialize(it) }
        return EReturnStatement(returnStatement.location, type, expression)
    }

    private fun specializeFunctionLiteralExpression(
        functionLiteralExpression: EFunctionLiteralExpression,
        specializeContext: SpecializeContext
    ): EFunctionLiteralExpression {
        val valueParameters = functionLiteralExpression.valueParameters.map { specializeContext.specialize(it) }
        val body = specializeContext.specialize(functionLiteralExpression.body)
        return EFunctionLiteralExpression(functionLiteralExpression.location, ArrayList(valueParameters), body)
    }

    private fun specializeStringTemplateExpression(
        stringTemplateExpression: EStringTemplateExpression,
        specializeContext: SpecializeContext
    ): EStringTemplateExpression {
        val entries = stringTemplateExpression.entries.map { entry ->
            when (entry) {
                is LiteralStringEntry -> LiteralStringEntry(entry.text)
                is ExpressionStringEntry -> ExpressionStringEntry(specializeContext.specialize(entry.expression))
            }
        }
        return EStringTemplateExpression(stringTemplateExpression.location, entries)
    }

    private fun specializeIsExpression(
        isExpression: EIsExpression,
        specializeContext: SpecializeContext
    ): EIsExpression {
        val expression = specializeContext.specialize(isExpression.expression)
        val property = specializeContext.specialize(isExpression.property)
        val castType = specializeContext.specializeType(isExpression.castType, isExpression)
        return EIsExpression(
            isExpression.location,
            expression,
            property,
            isExpression.isNegated,
            castType
        )
    }

    private fun specializeAsExpression(
        asExpression: EAsExpression,
        specializeContext: SpecializeContext
    ): EAsExpression {
        val type = specializeContext.specializeType(asExpression)
        val expression = specializeContext.specialize(asExpression.expression)
        return EAsExpression(asExpression.location, type, expression)
    }

    private fun specializeIfExpression(
        ifExpression: EIfExpression,
        specializeContext: SpecializeContext
    ): EIfExpression {
        val type = specializeContext.specializeType(ifExpression)
        val condition = specializeContext.specialize(ifExpression.condition)
        val thenExpression = ifExpression.thenExpression?.let { specializeContext.specialize(it) }
        val elseExpression = ifExpression.elseExpression?.let { specializeContext.specialize(it) }
        return EIfExpression(ifExpression.location, type, condition, thenExpression, elseExpression)
    }

    private fun specializeWhenExpression(
        whenExpression: EWhenExpression,
        specializeContext: SpecializeContext
    ): EWhenExpression {
        val type = specializeContext.specializeType(whenExpression)
        val subject = whenExpression.subject?.let { specializeContext.specialize(it) }
        val entries = whenExpression.entries.map { entry ->
            val conditions = entry.conditions.map { specializeContext.specialize(it) }
            val body = specializeContext.specialize(entry.body)
            WhenEntry(ArrayList(conditions), body)
        }
        return EWhenExpression(
            whenExpression.location,
            whenExpression.endLocation,
            type,
            subject,
            entries
        )
    }

    private fun specializeWhileStatement(
        whileStatement: EWhileStatement,
        specializeContext: SpecializeContext
    ): EWhileStatement {
        val condition = specializeContext.specialize(whileStatement.condition)
        val body = specializeContext.specialize(whileStatement.body)
        return EWhileStatement(whileStatement.location, condition, body, whileStatement.isDoWhile)
    }
}
