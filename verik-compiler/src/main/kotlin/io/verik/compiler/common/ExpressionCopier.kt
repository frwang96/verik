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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.message.Messages

object ExpressionCopier {

    fun <E : EExpression> copy(expression: E): E {
        val copiedExpression = when (expression) {
            is EKtBinaryExpression -> copyKtBinaryExpression(expression)
            is EReferenceExpression -> copyReferenceExpression(expression)
            is EKtCallExpression -> copyKtCallExpression(expression)
            is EConstantExpression -> copyConstantExpression(expression)
            is EInlineIfExpression -> copyInlineIfExpression(expression)
            else -> Messages.INTERNAL_ERROR.on(expression, "Unable to copy expression: $expression")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedExpression as E
    }

    private fun copyKtBinaryExpression(binaryExpression: EKtBinaryExpression): EKtBinaryExpression {
        val type = binaryExpression.type.copy()
        val left = copy(binaryExpression.left)
        val right = copy(binaryExpression.right)
        return EKtBinaryExpression(
            binaryExpression.location,
            type,
            left,
            right,
            binaryExpression.kind
        )
    }

    private fun copyReferenceExpression(referenceExpression: EReferenceExpression): EReferenceExpression {
        val type = referenceExpression.type.copy()
        val receiver = referenceExpression.receiver?.let { copy(it) }
        return EReferenceExpression(referenceExpression.location, type, referenceExpression.reference, receiver)
    }

    private fun copyKtCallExpression(callExpression: EKtCallExpression): EKtCallExpression {
        val type = callExpression.type.copy()
        val receiver = callExpression.receiver?.let { copy(it) }
        val valueArguments = callExpression.valueArguments.map { copy(it) }
        val typeArguments = callExpression.typeArguments.map { it.copy() }
        return EKtCallExpression(
            callExpression.location,
            type,
            callExpression.reference,
            receiver,
            ArrayList(valueArguments),
            ArrayList(typeArguments)
        )
    }

    private fun copyConstantExpression(constantExpression: EConstantExpression): EConstantExpression {
        val type = constantExpression.type.copy()
        return EConstantExpression(constantExpression.location, type, constantExpression.value)
    }

    private fun copyInlineIfExpression(inlineIfExpression: EInlineIfExpression): EInlineIfExpression {
        val type = inlineIfExpression.type.copy()
        val condition = copy(inlineIfExpression.condition)
        val thenExpression = copy(inlineIfExpression.thenExpression)
        val elseExpression = copy(inlineIfExpression.elseExpression)
        return EInlineIfExpression(
            inlineIfExpression.location,
            type,
            condition,
            thenExpression,
            elseExpression
        )
    }
}
