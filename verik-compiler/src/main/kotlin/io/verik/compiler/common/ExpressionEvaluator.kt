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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.constant.BooleanConstantEvaluator
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.CoreFunctionDeclaration

object ExpressionEvaluator {

    fun evaluate(expression: EExpression): EExpression? {
        return when (expression) {
            is EKtBinaryExpression ->
                evaluateBinaryExpression(expression)
            is EKtCallExpression -> {
                val reference = expression.reference
                if (reference is CoreFunctionDeclaration) {
                    reference.evaluate(expression)
                } else null
            }
            is EInlineIfExpression ->
                evaluateInlineIfExpression(expression)
            else -> null
        }
    }

    private fun evaluateBinaryExpression(binaryExpression: EKtBinaryExpression): EExpression? {
        val left = binaryExpression.left
        val right = binaryExpression.right
        return when (binaryExpression.kind) {
            KtBinaryOperatorKind.ANDAND -> BooleanConstantEvaluator.binaryAndBoolean(binaryExpression, left, right)
            KtBinaryOperatorKind.OROR -> BooleanConstantEvaluator.binaryOrBoolean(binaryExpression, left, right)
            else -> null
        }
    }

    private fun evaluateInlineIfExpression(inlineIfExpression: EInlineIfExpression): EExpression? {
        return when (ConstantNormalizer.parseBooleanOrNull(inlineIfExpression.condition)) {
            true -> inlineIfExpression.thenExpression
            false -> inlineIfExpression.elseExpression
            else -> null
        }
    }
}
