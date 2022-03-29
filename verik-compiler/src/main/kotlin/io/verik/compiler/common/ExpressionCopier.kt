/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.common

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.ast.element.expression.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.ast.element.expression.sv.EInlineIfExpression
import io.verik.compiler.ast.element.expression.sv.EStreamingExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.common.ExpressionCopier.deepCopy
import io.verik.compiler.common.ExpressionCopier.shallowCopy
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

/**
 * Utility class that copies an expression. We can either do a [shallowCopy] or a [deepCopy].
 */
object ExpressionCopier {

    fun <E : EExpression> shallowCopy(expression: E): E {
        return copy(expression, false, null)
    }

    fun <E : EExpression> deepCopy(expression: E, location: SourceLocation? = null): E {
        return copy(expression, true, location)
    }

    private fun <E : EExpression> copy(expression: E, isDeepCopy: Boolean, location: SourceLocation?): E {
        val copiedExpression: EExpression = when (expression) {
            is ENothingExpression -> copyNothingExpression(expression, location)
            is EBlockExpression -> copyBlockExpression(expression, isDeepCopy, location)
            is EPropertyStatement -> copyPropertyStatement(expression, isDeepCopy, location)
            is EKtBinaryExpression -> copyKtBinaryExpression(expression, isDeepCopy, location)
            is ESvBinaryExpression -> copySvBinaryExpression(expression, isDeepCopy, location)
            is EReferenceExpression -> copyReferenceExpression(expression, isDeepCopy, location)
            is ECallExpression -> copyCallExpression(expression, isDeepCopy, location)
            is EConstantExpression -> copyConstantExpression(expression, isDeepCopy, location)
            is EFunctionLiteralExpression -> copyFunctionLiteralExpression(expression, isDeepCopy, location)
            is EStringTemplateExpression -> copyStringTemplateExpression(expression, isDeepCopy, location)
            is EKtArrayAccessExpression -> copyKtArrayAccessExpression(expression, isDeepCopy, location)
            is EConcatenationExpression -> copyConcatenationExpression(expression, isDeepCopy, location)
            is EStreamingExpression -> copyStreamingExpression(expression, isDeepCopy, location)
            is EIsExpression -> copyIsExpression(expression, isDeepCopy, location)
            is EIfExpression -> copyIfExpression(expression, isDeepCopy, location)
            is EInlineIfExpression -> copyInlineIfExpression(expression, isDeepCopy, location)
            is EWhenExpression -> copyWhenExpression(expression, isDeepCopy, location)
            else -> Messages.INTERNAL_ERROR.on(expression, "Unable to copy expression: $expression")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedExpression as E
    }

    private fun copyNothingExpression(
        nothingExpression: ENothingExpression,
        location: SourceLocation?
    ): ENothingExpression {
        return ENothingExpression(location ?: nothingExpression.location)
    }

    private fun copyBlockExpression(
        blockExpression: EBlockExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EBlockExpression {
        return if (isDeepCopy) {
            val type = blockExpression.type.copy()
            val statements = blockExpression.statements.map { copy(it, true, location) }
            EBlockExpression(
                location ?: blockExpression.location,
                blockExpression.endLocation,
                type,
                ArrayList(statements)
            )
        } else {
            EBlockExpression(
                location ?: blockExpression.location,
                blockExpression.endLocation,
                blockExpression.type,
                blockExpression.statements
            )
        }
    }

    private fun copyPropertyStatement(
        propertyStatement: EPropertyStatement,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EPropertyStatement {
        return if (isDeepCopy) {
            val type = propertyStatement.property.type.copy()
            val initializer = propertyStatement.property.initializer?.let { copy(it, true, location) }
            // TODO replace references to property
            val property = EProperty(
                location ?: propertyStatement.property.location,
                propertyStatement.property.endLocation,
                propertyStatement.property.name,
                type,
                propertyStatement.property.annotationEntries,
                propertyStatement.property.documentationLines,
                initializer,
                propertyStatement.property.isMutable,
                propertyStatement.property.isStatic
            )
            EPropertyStatement(
                location ?: propertyStatement.location,
                property
            )
        } else {
            EPropertyStatement(
                location ?: propertyStatement.location,
                propertyStatement.property
            )
        }
    }

    private fun copyKtBinaryExpression(
        binaryExpression: EKtBinaryExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EKtBinaryExpression {
        return if (isDeepCopy) {
            val type = binaryExpression.type.copy()
            val left = copy(binaryExpression.left, true, location)
            val right = copy(binaryExpression.right, true, location)
            EKtBinaryExpression(
                location ?: binaryExpression.location,
                type,
                left,
                right,
                binaryExpression.kind
            )
        } else {
            EKtBinaryExpression(
                location ?: binaryExpression.location,
                binaryExpression.type,
                binaryExpression.left,
                binaryExpression.right,
                binaryExpression.kind
            )
        }
    }

    private fun copySvBinaryExpression(
        binaryExpression: ESvBinaryExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): ESvBinaryExpression {
        return if (isDeepCopy) {
            val type = binaryExpression.type.copy()
            val left = copy(binaryExpression.left, true, location)
            val right = copy(binaryExpression.right, true, location)
            ESvBinaryExpression(
                location ?: binaryExpression.location,
                type,
                left,
                right,
                binaryExpression.kind
            )
        } else {
            ESvBinaryExpression(
                location ?: binaryExpression.location,
                binaryExpression.type,
                binaryExpression.left,
                binaryExpression.right,
                binaryExpression.kind
            )
        }
    }

    private fun copyReferenceExpression(
        referenceExpression: EReferenceExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EReferenceExpression {
        return if (isDeepCopy) {
            val type = referenceExpression.type.copy()
            val receiver = referenceExpression.receiver?.let { copy(it, true, location) }
            EReferenceExpression(
                location ?: referenceExpression.location,
                type,
                referenceExpression.reference,
                receiver,
                referenceExpression.isSafeAccess
            )
        } else {
            EReferenceExpression(
                location ?: referenceExpression.location,
                referenceExpression.type,
                referenceExpression.reference,
                referenceExpression.receiver,
                referenceExpression.isSafeAccess
            )
        }
    }

    private fun copyCallExpression(
        callExpression: ECallExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): ECallExpression {
        return if (isDeepCopy) {
            val type = callExpression.type.copy()
            val receiver = callExpression.receiver?.let { copy(it, true, location) }
            val valueArguments = callExpression.valueArguments.map { copy(it, true, location) }
            val typeArguments = callExpression.typeArguments.map { it.copy() }
            ECallExpression(
                location ?: callExpression.location,
                type,
                callExpression.reference,
                receiver,
                callExpression.isSafeAccess,
                ArrayList(valueArguments),
                ArrayList(typeArguments)
            )
        } else {
            ECallExpression(
                location ?: callExpression.location,
                callExpression.type,
                callExpression.reference,
                callExpression.receiver,
                callExpression.isSafeAccess,
                callExpression.valueArguments,
                callExpression.typeArguments
            )
        }
    }

    private fun copyConstantExpression(
        constantExpression: EConstantExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EConstantExpression {
        return if (isDeepCopy) {
            val type = constantExpression.type.copy()
            EConstantExpression(
                location ?: constantExpression.location,
                type,
                constantExpression.value
            )
        } else {
            EConstantExpression(
                location ?: constantExpression.location,
                constantExpression.type,
                constantExpression.value
            )
        }
    }

    private fun copyFunctionLiteralExpression(
        functionLiteralExpression: EFunctionLiteralExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EFunctionLiteralExpression {
        return if (isDeepCopy) {
            // TODO replace references to value parameters
            val valueParameters = ArrayList<EKtValueParameter>()
            functionLiteralExpression.valueParameters.forEach { valueParameter ->
                if (valueParameter is EKtValueParameter) {
                    val expression = valueParameter.expression?.let { copy(it, true, location) }
                    val copiedValueParameter = EKtValueParameter(
                        valueParameter.location,
                        valueParameter.name,
                        valueParameter.type.copy(),
                        valueParameter.annotationEntries,
                        expression,
                        valueParameter.isPrimaryConstructorProperty,
                        valueParameter.isMutable
                    )
                    valueParameters.add(copiedValueParameter)
                }
            }
            val body = copy(functionLiteralExpression.body, true, location)
            EFunctionLiteralExpression(
                location ?: functionLiteralExpression.location,
                ArrayList(valueParameters),
                body
            )
        } else {
            EFunctionLiteralExpression(
                location ?: functionLiteralExpression.location,
                functionLiteralExpression.valueParameters,
                functionLiteralExpression.body
            )
        }
    }

    private fun copyStringTemplateExpression(
        stringTemplateExpression: EStringTemplateExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EStringTemplateExpression {
        return if (isDeepCopy) {
            val entries = stringTemplateExpression.entries.map {
                when (it) {
                    is LiteralStringEntry -> LiteralStringEntry(it.text)
                    is ExpressionStringEntry -> ExpressionStringEntry(copy(it.expression, true, location))
                }
            }
            EStringTemplateExpression(
                location ?: stringTemplateExpression.location,
                entries
            )
        } else {
            EStringTemplateExpression(
                location ?: stringTemplateExpression.location,
                stringTemplateExpression.entries
            )
        }
    }

    private fun copyKtArrayAccessExpression(
        arrayAccessExpression: EKtArrayAccessExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EKtArrayAccessExpression {
        return if (isDeepCopy) {
            val type = arrayAccessExpression.type.copy()
            val array = copy(arrayAccessExpression.array, true, location)
            val indices = arrayAccessExpression.indices.map { copy(it, true, location) }
            EKtArrayAccessExpression(
                location ?: arrayAccessExpression.location,
                type,
                array,
                ArrayList(indices)
            )
        } else {
            EKtArrayAccessExpression(
                location ?: arrayAccessExpression.location,
                arrayAccessExpression.type,
                arrayAccessExpression.array,
                arrayAccessExpression.indices
            )
        }
    }

    private fun copyConcatenationExpression(
        concatenationExpression: EConcatenationExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EConcatenationExpression {
        return if (isDeepCopy) {
            val type = concatenationExpression.type.copy()
            val expressions = concatenationExpression.expressions.map { copy(it, true, location) }
            EConcatenationExpression(
                location ?: concatenationExpression.location,
                type,
                ArrayList(expressions)
            )
        } else {
            EConcatenationExpression(
                location ?: concatenationExpression.location,
                concatenationExpression.type,
                concatenationExpression.expressions
            )
        }
    }

    private fun copyStreamingExpression(
        streamingExpression: EStreamingExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EStreamingExpression {
        return if (isDeepCopy) {
            val type = streamingExpression.type.copy()
            val expression = copy(streamingExpression.expression, true, location)
            EStreamingExpression(
                location ?: streamingExpression.location,
                type,
                expression
            )
        } else {
            EStreamingExpression(
                location ?: streamingExpression.location,
                streamingExpression.type,
                streamingExpression.expression
            )
        }
    }

    private fun copyIsExpression(
        isExpression: EIsExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EIsExpression {
        return if (isDeepCopy) {
            val expression = copy(isExpression.expression, true, location)
            val castType = isExpression.castType.copy()
            EIsExpression(
                location ?: isExpression.location,
                expression,
                isExpression.property,
                isExpression.isNegated,
                castType
            )
        } else {
            EIsExpression(
                location ?: isExpression.location,
                isExpression.expression,
                isExpression.property,
                isExpression.isNegated,
                isExpression.castType
            )
        }
    }

    private fun copyIfExpression(
        ifExpression: EIfExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EIfExpression {
        return if (isDeepCopy) {
            val type = ifExpression.type.copy()
            val condition = copy(ifExpression.condition, true, location)
            val thenExpression = ifExpression.thenExpression?.let { copy(it, true, location) }
            val elseExpression = ifExpression.elseExpression?.let { copy(it, true, location) }
            EIfExpression(
                location ?: ifExpression.location,
                type,
                condition,
                thenExpression,
                elseExpression
            )
        } else {
            EIfExpression(
                location ?: ifExpression.location,
                ifExpression.type,
                ifExpression.condition,
                ifExpression.elseExpression,
                ifExpression.thenExpression
            )
        }
    }

    private fun copyInlineIfExpression(
        inlineIfExpression: EInlineIfExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EInlineIfExpression {
        return if (isDeepCopy) {
            val type = inlineIfExpression.type.copy()
            val condition = copy(inlineIfExpression.condition, true, location)
            val thenExpression = copy(inlineIfExpression.thenExpression, true, location)
            val elseExpression = copy(inlineIfExpression.elseExpression, true, location)
            EInlineIfExpression(
                location ?: inlineIfExpression.location,
                type,
                condition,
                thenExpression,
                elseExpression
            )
        } else {
            EInlineIfExpression(
                location ?: inlineIfExpression.location,
                inlineIfExpression.type,
                inlineIfExpression.condition,
                inlineIfExpression.thenExpression,
                inlineIfExpression.elseExpression
            )
        }
    }

    private fun copyWhenExpression(
        whenExpression: EWhenExpression,
        isDeepCopy: Boolean,
        location: SourceLocation?
    ): EWhenExpression {
        return if (isDeepCopy) {
            val type = whenExpression.type.copy()
            val subject = whenExpression.subject?.let { copy(it, true, location) }
            val entries = whenExpression.entries.map { whenEntry ->
                val conditions = whenEntry.conditions.map { copy(it, true, location) }
                val body = copy(whenEntry.body, true, location)
                WhenEntry(ArrayList(conditions), body)
            }
            return EWhenExpression(
                location ?: whenExpression.location,
                whenExpression.endLocation,
                type,
                subject,
                entries
            )
        } else {
            EWhenExpression(
                location ?: whenExpression.location,
                whenExpression.endLocation,
                whenExpression.type,
                whenExpression.subject,
                whenExpression.entries
            )
        }
    }
}
