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

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages

object TypeConstraintChecker {

    fun check(typeConstraints: List<TypeConstraint>) {
        typeConstraints.forEach {
            when (it) {
                is TypeEqualsTypeConstraint -> checkTypeEqualsTypeConstraint(it)
                is UnaryOperatorTypeConstraint -> checkUnaryOperatorTypeConstraint(it)
                is BinaryOperatorTypeConstraint -> checkBinaryOperatorTypeConstraint(it)
                is ComparisonTypeConstraint -> checkComparisonTypeConstraint(it)
                is ConcatenationTypeConstraint -> checkConcatenationTypeConstraint(it)
                is ReplicationTypeConstraint -> checkReplicationTypeConstraint(it)
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
                val substitutionResult = typeConstraint.outer.substitute(Cardinal.of(expectedValue).toType())
                Messages.TYPE_MISMATCH.on(
                    typeConstraint.outer.getElement(),
                    substitutionResult.original,
                    substitutionResult.substituted
                )
            }
        } else {
            val expectedValue = typeConstraint.kind.evaluate(outerValue)
            if (expectedValue != innerValue) {
                val substitutionResult = typeConstraint.inner.substitute(Cardinal.of(expectedValue).toType())
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
            val substitutionResult = typeConstraint.outer.substitute(Cardinal.of(innerValue).toType())
            Messages.TYPE_MISMATCH.on(
                typeConstraint.outer.getElement(),
                substitutionResult.original,
                substitutionResult.substituted
            )
        }
    }

    private fun checkComparisonTypeConstraint(typeConstraint: ComparisonTypeConstraint) {
        val innerValue = typeConstraint.inner.getType().asCardinalValue(typeConstraint.inner.getElement())
        val outerValue = typeConstraint.outer.getType().asCardinalValue(typeConstraint.outer.getElement())
        when (typeConstraint.kind) {
            ComparisonTypeConstraintKind.EXT -> {
                if (innerValue > outerValue) {
                    val substitutionResult = typeConstraint.inner.substitute(Cardinal.of(outerValue).toType())
                    Messages.EXTENSION_ERROR.on(
                        typeConstraint.inner.getElement(),
                        substitutionResult.original,
                        substitutionResult.substituted
                    )
                }
            }
            ComparisonTypeConstraintKind.TRU -> {
                if (innerValue < outerValue) {
                    val substitutionResult = typeConstraint.inner.substitute(Cardinal.of(outerValue).toType())
                    Messages.TRUNCATION_ERROR.on(
                        typeConstraint.inner.getElement(),
                        substitutionResult.original,
                        substitutionResult.substituted
                    )
                }
            }
        }
    }

    private fun checkConcatenationTypeConstraint(typeConstraint: ConcatenationTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type.asBitWidth(typeConstraint.callExpression)
        val valueArgumentWidths = typeConstraint
            .callExpression
            .valueArguments
            .map { getTypeWidth(it.type, it) }
        val sumWidth = valueArgumentWidths.sum()
        if (sumWidth != expressionWidth) {
            val actualType = Core.Vk.C_Ubit.toType(Cardinal.of(sumWidth).toType())
            Messages.TYPE_MISMATCH.on(typeConstraint.callExpression, typeConstraint.callExpression.type, actualType)
        }
    }

    private fun checkReplicationTypeConstraint(typeConstraint: ReplicationTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type.asBitWidth(typeConstraint.callExpression)
        val valueArgumentWidth = typeConstraint.callExpression.valueArguments[0]
            .let { getTypeWidth(it.type, it) }
        val typeArgumentWidth = typeConstraint.callExpression
            .typeArguments[0].asCardinalValue(typeConstraint.callExpression)
        if (expressionWidth != valueArgumentWidth * typeArgumentWidth) {
            val actualType = Core.Vk.C_Ubit.toType(
                Cardinal.of(valueArgumentWidth * typeArgumentWidth).toType()
            )
            Messages.TYPE_MISMATCH.on(typeConstraint.callExpression, typeConstraint.callExpression.type, actualType)
        }
    }

    private fun getTypeWidth(type: Type, element: EElement): Int {
        return when (type.reference) {
            Core.Kt.C_Boolean -> 1
            Core.Vk.C_Ubit -> type.asBitWidth(element)
            Core.Vk.C_Sbit -> type.asBitWidth(element)
            else -> 0
        }
    }
}
