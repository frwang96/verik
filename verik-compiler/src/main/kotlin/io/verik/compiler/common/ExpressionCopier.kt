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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

object ExpressionCopier {

    fun <E : EExpression> shallowCopy(expression: E): E {
        return copy(expression, false, null)
    }

    fun <E : EExpression> deepCopy(expression: E): E {
        return copy(expression, true, null)
    }

    fun <E : EExpression> deepCopy(expression: E, location: SourceLocation): E {
        return copy(expression, true, location)
    }

    private fun <E : EExpression> copy(expression: E, isDeepCopy: Boolean, location: SourceLocation?): E {
        val copiedExpression: EExpression = when (expression) {
            is EBlockExpression -> copyBlockExpression(expression, isDeepCopy, location)
            is EKtBinaryExpression -> copyKtBinaryExpression(expression, isDeepCopy, location)
            is ESvBinaryExpression -> copySvBinaryExpression(expression, isDeepCopy, location)
            is EReferenceExpression -> copyReferenceExpression(expression, isDeepCopy, location)
            is ECallExpression -> copyCallExpression(expression, isDeepCopy, location)
            is EConstantExpression -> copyConstantExpression(expression, isDeepCopy, location)
            is EKtArrayAccessExpression -> copyKtArrayAccessExpression(expression, isDeepCopy, location)
            is EConcatenationExpression -> copyConcatenationExpression(expression, isDeepCopy, location)
            is EStreamingExpression -> copyStreamingExpression(expression, isDeepCopy, location)
            is EIsExpression -> copyIsExpression(expression, isDeepCopy, location)
            is EIfExpression -> copyIfExpression(expression, isDeepCopy, location)
            is EInlineIfExpression -> copyInlineIfExpression(expression, isDeepCopy, location)
            else -> Messages.INTERNAL_ERROR.on(expression, "Unable to copy expression: $expression")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedExpression as E
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
                blockExpression.location,
                blockExpression.endLocation,
                type,
                ArrayList(statements)
            )
        } else {
            EBlockExpression(
                blockExpression.location,
                blockExpression.endLocation,
                blockExpression.type,
                blockExpression.statements
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
                binaryExpression.location,
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
                binaryExpression.location,
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
                receiver
            )
        } else {
            EReferenceExpression(
                referenceExpression.location,
                referenceExpression.type,
                referenceExpression.reference,
                referenceExpression.receiver
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
                ArrayList(valueArguments),
                ArrayList(typeArguments)
            )
        } else {
            ECallExpression(
                callExpression.location,
                callExpression.type,
                callExpression.reference,
                callExpression.receiver,
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
                constantExpression.location,
                constantExpression.type,
                constantExpression.value
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
                arrayAccessExpression.location,
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
                concatenationExpression.location,
                type,
                ArrayList(expressions)
            )
        } else {
            EConcatenationExpression(
                concatenationExpression.location,
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
                streamingExpression.location,
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
                isExpression.location,
                expression,
                isExpression.property,
                isExpression.isNegated,
                castType
            )
        } else {
            EIsExpression(
                isExpression.location,
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
                ifExpression.location,
                type,
                condition,
                thenExpression,
                elseExpression
            )
        } else {
            EIfExpression(
                ifExpression.location,
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
                inlineIfExpression.location,
                inlineIfExpression.type,
                inlineIfExpression.condition,
                inlineIfExpression.thenExpression,
                inlineIfExpression.elseExpression
            )
        }
    }
}
