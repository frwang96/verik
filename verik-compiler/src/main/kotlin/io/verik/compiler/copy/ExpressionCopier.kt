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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.message.Messages

object ExpressionCopier {

    fun <E : EExpression> copyExpression(expression: E, copyContext: CopyContext): E {
        val copiedExpression = when (expression) {
            is EKtBlockExpression -> copyKtBlockExpression(expression, copyContext)
            is EKtReferenceExpression -> copyKtReferenceExpression(expression, copyContext)
            is EKtCallExpression -> copyKtCallExpression(expression, copyContext)
            is EConstantExpression -> copyConstantExpression(expression, copyContext)
            else -> {
                Messages.INTERNAL_ERROR.on(expression, "Unable to copy expression: $expression")
                expression
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedExpression as E
    }

    private fun copyKtBlockExpression(
        blockExpression: EKtBlockExpression,
        copyContext: CopyContext
    ): EKtBlockExpression {
        val type = copyContext.copy(blockExpression.type)
        val statements = blockExpression.statements.map { copyContext.copy(it) }
        return EKtBlockExpression(blockExpression.location, type, ArrayList(statements))
    }

    private fun copyKtReferenceExpression(
        referenceExpression: EKtReferenceExpression,
        copyContext: CopyContext
    ): EKtReferenceExpression {
        val type = copyContext.copy(referenceExpression.type)
        val receiver = referenceExpression.receiver?.let { copyContext.copy(it) }
        return EKtReferenceExpression(referenceExpression.location, type, referenceExpression.reference, receiver)
    }

    private fun copyKtCallExpression(callExpression: EKtCallExpression, copyContext: CopyContext): EKtCallExpression {
        val type = copyContext.copy(callExpression.type)
        val receiver = callExpression.receiver?.let { copyContext.copy(it) }
        val valueArguments = callExpression.valueArguments.map { copyContext.copy(it) }
        val typeArguments = callExpression.typeArguments.map { copyContext.copy(it) }
        return EKtCallExpression(
            callExpression.location,
            type,
            callExpression.reference,
            receiver,
            ArrayList(valueArguments),
            ArrayList(typeArguments)
        )
    }

    private fun copyConstantExpression(
        constantExpression: EConstantExpression,
        copyContext: CopyContext
    ): EConstantExpression {
        val type = copyContext.copy(constantExpression.type)
        return EConstantExpression(constantExpression.location, type, constantExpression.value)
    }
}
