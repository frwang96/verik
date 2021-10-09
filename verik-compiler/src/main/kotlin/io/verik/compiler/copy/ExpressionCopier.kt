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
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.EWhileExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
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

object ExpressionCopier {

    fun <E : EExpression> copyExpression(expression: E, copyContext: CopyContext): E {
        val copiedExpression = when (expression) {
            is EKtBlockExpression -> copyKtBlockExpression(expression, copyContext)
            is EPropertyStatement -> copyPropertyStatement(expression, copyContext)
            is EKtUnaryExpression -> copyKtUnaryExpression(expression, copyContext)
            is EKtBinaryExpression -> copyKtBinaryExpression(expression, copyContext)
            is EKtReferenceExpression -> copyKtReferenceExpression(expression, copyContext)
            is EKtCallExpression -> copyKtCallExpression(expression, copyContext)
            is EConstantExpression -> copyConstantExpression(expression, copyContext)
            is EReturnStatement -> copyReturnStatement(expression, copyContext)
            is EFunctionLiteralExpression -> copyFunctionLiteralExpression(expression, copyContext)
            is EStringTemplateExpression -> copyStringTemplateExpression(expression, copyContext)
            is EIfExpression -> copyIfExpression(expression, copyContext)
            is EWhenExpression -> copyWhenExpression(expression, copyContext)
            is EWhileExpression -> copyWhileExpression(expression, copyContext)
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

    private fun copyPropertyStatement(
        propertyStatement: EPropertyStatement,
        copyContext: CopyContext
    ): EPropertyStatement {
        val property = copyContext.copy(propertyStatement.property)
        return EPropertyStatement(propertyStatement.location, property)
    }

    private fun copyKtUnaryExpression(
        unaryExpression: EKtUnaryExpression,
        copyContext: CopyContext
    ): EKtUnaryExpression {
        val type = copyContext.copy(unaryExpression.type)
        val expression = copyContext.copy(unaryExpression.expression)
        return EKtUnaryExpression(unaryExpression.location, type, expression, unaryExpression.kind)
    }

    private fun copyKtBinaryExpression(
        binaryExpression: EKtBinaryExpression,
        copyContext: CopyContext
    ): EKtBinaryExpression {
        val type = copyContext.copy(binaryExpression.type)
        val left = copyContext.copy(binaryExpression.left)
        val right = copyContext.copy(binaryExpression.right)
        return EKtBinaryExpression(binaryExpression.location, type, left, right, binaryExpression.kind)
    }

    private fun copyKtReferenceExpression(
        referenceExpression: EKtReferenceExpression,
        copyContext: CopyContext
    ): EKtReferenceExpression {
        val type = copyContext.copy(referenceExpression.type)
        val reference = copyContext.get(referenceExpression.reference) ?: referenceExpression.reference
        val receiver = referenceExpression.receiver?.let { copyContext.copy(it) }
        return EKtReferenceExpression(referenceExpression.location, type, reference, receiver)
    }

    private fun copyKtCallExpression(callExpression: EKtCallExpression, copyContext: CopyContext): EKtCallExpression {
        val type = copyContext.copy(callExpression.type)
        val reference = copyContext.get(callExpression.reference) ?: callExpression.reference
        val receiver = callExpression.receiver?.let { copyContext.copy(it) }
        val valueArguments = callExpression.valueArguments.map { copyContext.copy(it) }
        val typeArguments = callExpression.typeArguments.map { copyContext.copy(it) }
        return EKtCallExpression(
            callExpression.location,
            type,
            reference,
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

    private fun copyReturnStatement(returnStatement: EReturnStatement, copyContext: CopyContext): EReturnStatement {
        val type = copyContext.copy(returnStatement.type)
        val expression = returnStatement.expression?.let { copyContext.copy(it) }
        return EReturnStatement(returnStatement.location, type, expression)
    }

    private fun copyFunctionLiteralExpression(
        functionLiteralExpression: EFunctionLiteralExpression,
        copyContext: CopyContext
    ): EFunctionLiteralExpression {
        val valueParameters = functionLiteralExpression.valueParameters.map { copyContext.copy(it) }
        val body = copyContext.copy(functionLiteralExpression.body)
        return EFunctionLiteralExpression(functionLiteralExpression.location, ArrayList(valueParameters), body)
    }

    private fun copyStringTemplateExpression(
        stringTemplateExpression: EStringTemplateExpression,
        copyContext: CopyContext
    ): EStringTemplateExpression {
        val entries = stringTemplateExpression.entries.map { entry ->
            when (entry) {
                is LiteralStringEntry -> LiteralStringEntry(entry.text)
                is ExpressionStringEntry -> ExpressionStringEntry(copyContext.copy(entry.expression))
            }
        }
        return EStringTemplateExpression(stringTemplateExpression.location, entries)
    }

    private fun copyIfExpression(ifExpression: EIfExpression, copyContext: CopyContext): EIfExpression {
        val type = copyContext.copy(ifExpression.type)
        val condition = copyContext.copy(ifExpression.condition)
        val thenExpression = ifExpression.thenExpression?.let { copyContext.copy(it) }
        val elseExpression = ifExpression.elseExpression?.let { copyContext.copy(it) }
        return EIfExpression(ifExpression.location, type, condition, thenExpression, elseExpression)
    }

    private fun copyWhenExpression(whenExpression: EWhenExpression, copyContext: CopyContext): EWhenExpression {
        val type = copyContext.copy(whenExpression.type)
        val subject = copyContext.copy(whenExpression.subject)
        val entries = whenExpression.entries.map { entry ->
            val conditions = entry.conditions.map { copyContext.copy(it) }
            val body = copyContext.copy(entry.body)
            WhenEntry(ArrayList(conditions), body)
        }
        return EWhenExpression(whenExpression.location, type, subject, entries)
    }

    private fun copyWhileExpression(whileExpression: EWhileExpression, copyContext: CopyContext): EWhileExpression {
        val condition = copyContext.copy(whileExpression.condition)
        val body = copyContext.copy(whileExpression.body)
        return EWhileExpression(whileExpression.location, condition, body, whileExpression.isDoWhile)
    }
}
