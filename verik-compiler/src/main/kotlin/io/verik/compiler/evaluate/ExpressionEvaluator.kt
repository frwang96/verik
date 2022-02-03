/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.constant.BooleanConstantEvaluator
import io.verik.compiler.constant.BooleanConstantKind
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.CoreFunctionDeclaration

object ExpressionEvaluator {

    fun evaluate(expression: EExpression): EExpression? {
        return when (expression) {
            is EKtBinaryExpression -> evaluateBinaryExpression(expression)
            is ECallExpression -> evaluateCallExpression(expression)
            is EIfExpression -> evaluateIfExpression(expression)
            is EWhenExpression -> evaluateWhenExpression(expression)
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

    private fun evaluateCallExpression(callExpression: ECallExpression): EExpression? {
        val reference = callExpression.reference
        return if (reference is CoreFunctionDeclaration) {
            reference.evaluate(callExpression)
        } else null
    }

    private fun evaluateIfExpression(ifExpression: EIfExpression): EExpression? {
        return when (ConstantNormalizer.parseBooleanOrNull(ifExpression.condition)) {
            BooleanConstantKind.TRUE -> {
                val thenExpression = ifExpression.thenExpression
                when {
                    thenExpression == null -> EBlockExpression.empty(ifExpression.location)
                    thenExpression.statements.size == 1 -> thenExpression.statements[0]
                    else -> thenExpression
                }
            }
            BooleanConstantKind.FALSE -> {
                val elseExpression = ifExpression.elseExpression
                when {
                    elseExpression == null -> EBlockExpression.empty(ifExpression.location)
                    elseExpression.statements.size == 1 -> elseExpression.statements[0]
                    else -> elseExpression
                }
            }
            else -> null
        }
    }

    private fun evaluateWhenExpression(whenExpression: EWhenExpression): EExpression? {
        if (whenExpression.subject != null)
            return null
        val entries = ArrayList<WhenEntry>()
        for (entry in whenExpression.entries) {
            val evaluatedConditions = entry.conditions.map { ConstantNormalizer.parseBooleanOrNull(it) }
            if (evaluatedConditions.isEmpty() || evaluatedConditions.any { it == BooleanConstantKind.TRUE }) {
                entries.add(WhenEntry(ArrayList(), entry.body))
                break
            }
            if (!evaluatedConditions.all { it == BooleanConstantKind.FALSE }) {
                entries.add(entry)
            }
        }
        return when (entries.size) {
            0 -> EBlockExpression.empty(whenExpression.location)
            whenExpression.entries.size -> null
            else -> {
                EWhenExpression(
                    whenExpression.location,
                    whenExpression.endLocation,
                    whenExpression.type.copy(),
                    null,
                    entries
                )
            }
        }
    }
}
