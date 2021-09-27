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

import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages

object TypeConstraintChecker {

    fun check(typeConstraints: List<TypeConstraint>) {
        typeConstraints.forEach {
            when (it) {
                is TypeEqualsTypeConstraint ->
                    checkTypeEqualsTypeConstraint(it)
                is UnaryOperatorTypeConstraint ->
                    checkUnaryOperatorTypeConstraint(it)
                is BinaryOperatorTypeConstraint ->
                    checkBinaryOperatorTypeConstraint(it)
                is ConcatenationTypeConstraint ->
                    checkConcatenationTypeConstraint(it)
            }
        }
    }

    private fun checkTypeEqualsTypeConstraint(typeConstraint: TypeEqualsTypeConstraint) {
        val innerType = typeConstraint.inner.getType()
        val outerType = typeConstraint.outer.getType()
        if (outerType != innerType) {
            val substitutionResult = typeConstraint.inner.substitute(outerType)
            Messages.TYPE_MISMATCH.on(
                typeConstraint.inner.getElement(),
                substitutionResult.substituted,
                substitutionResult.original
            )
        }
    }

    private fun checkUnaryOperatorTypeConstraint(typeConstraint: UnaryOperatorTypeConstraint) {
        val innerValue = typeConstraint.inner.getType().asCardinalValue(typeConstraint.inner.getElement())
        val outerValue = typeConstraint.outer.getType().asCardinalValue(typeConstraint.outer.getElement())
        if (typeConstraint.isInnerToOuter) {
            val expectedValue = typeConstraint.kind.evaluate(innerValue)
            if (expectedValue != outerValue) {
                val substitutionResult = typeConstraint.outer.substitute(Core.Vk.cardinalOf(expectedValue).toType())
                Messages.TYPE_MISMATCH.on(
                    typeConstraint.outer.getElement(),
                    substitutionResult.original,
                    substitutionResult.substituted
                )
            }
        } else {
            val expectedValue = typeConstraint.kind.evaluate(outerValue)
            if (expectedValue != innerValue) {
                val substitutionResult = typeConstraint.inner.substitute(Core.Vk.cardinalOf(expectedValue).toType())
                Messages.TYPE_MISMATCH.on(
                    typeConstraint.inner.getElement(),
                    substitutionResult.substituted,
                    substitutionResult.original
                )
            }
        }
    }

    private fun checkBinaryOperatorTypeConstraint(typeConstraint: BinaryOperatorTypeConstraint) {
        val leftValue = typeConstraint.left.getType().asCardinalValue(typeConstraint.left.getElement())
        val rightValue = typeConstraint.right.getType().asCardinalValue(typeConstraint.right.getElement())
        val innerValue = typeConstraint.kind.evaluate(leftValue, rightValue)
        val outerValue = typeConstraint.outer.getType().asCardinalValue(typeConstraint.outer.getElement())
        if (outerValue != innerValue) {
            val substitutionResult = typeConstraint.outer.substitute(Core.Vk.cardinalOf(innerValue).toType())
            Messages.TYPE_MISMATCH.on(
                typeConstraint.outer.getElement(),
                substitutionResult.original,
                substitutionResult.substituted
            )
        }
    }

    private fun checkConcatenationTypeConstraint(typeConstraint: ConcatenationTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type
            .arguments[0].asCardinalValue(typeConstraint.callExpression)
        val valueArgumentWidths = typeConstraint
            .callExpression
            .valueArguments
            .map {
                when (it.type.reference) {
                    Core.Kt.C_Boolean -> 1
                    Core.Vk.C_Ubit -> it.type.arguments[0].asCardinalValue(it)
                    Core.Vk.C_Sbit -> it.type.arguments[0].asCardinalValue(it)
                    else -> 0
                }
            }
        val sumWidth = valueArgumentWidths.sum()
        if (sumWidth != expressionWidth) {
            val actualType = typeConstraint.callExpression.type.copy()
            actualType.arguments[0] = Core.Vk.cardinalOf(sumWidth).toType()
            Messages.TYPE_MISMATCH.on(typeConstraint.callExpression, typeConstraint.callExpression.type, actualType)
        }
    }
}
