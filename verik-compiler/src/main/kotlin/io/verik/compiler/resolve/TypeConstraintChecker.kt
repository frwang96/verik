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

import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages

object TypeConstraintChecker {

    fun check(typeConstraints: List<TypeConstraint>) {
        typeConstraints.forEach {
            when (it) {
                is EqualsTypeConstraint -> checkEqualsTypeConstraint(it)
                is UnaryTypeConstraint -> checkUnaryTypeConstraint(it)
                is BinaryTypeConstraint -> checkBinaryTypeConstraint(it)
                is SpecialTypeConstraint -> checkSpecialTypeConstraint(it)
                is ComparisonTypeConstraint -> checkComparisonTypeConstraint(it)
            }
        }
    }

    private fun checkEqualsTypeConstraint(typeConstraint: EqualsTypeConstraint) {
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

    private fun checkUnaryTypeConstraint(typeConstraint: UnaryTypeConstraint) {
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

    private fun checkBinaryTypeConstraint(typeConstraint: BinaryTypeConstraint) {
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

    private fun checkSpecialTypeConstraint(typeConstraint: SpecialTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type.asBitWidth(typeConstraint.callExpression)
        val valueArgumentWidths = typeConstraint
            .callExpression
            .valueArguments
            .map { it.type.getWidthAsInt(it) }
        when (typeConstraint.kind) {
            SpecialTypeConstraintKind.CAT -> {
                val catWidth = valueArgumentWidths.sum()
                if (catWidth != expressionWidth) {
                    val actualType = Core.Vk.C_Ubit.toType(Cardinal.of(catWidth).toType())
                    Messages.TYPE_MISMATCH.on(
                        typeConstraint.callExpression,
                        typeConstraint.callExpression.type,
                        actualType
                    )
                }
            }
            SpecialTypeConstraintKind.REP -> {
                val typeArgumentWidth = typeConstraint.callExpression
                    .typeArguments[0].asCardinalValue(typeConstraint.callExpression)
                val repWidth = valueArgumentWidths[0] * typeArgumentWidth
                if (expressionWidth != repWidth) {
                    val actualType = Core.Vk.C_Ubit.toType(Cardinal.of(repWidth).toType())
                    Messages.TYPE_MISMATCH.on(
                        typeConstraint.callExpression,
                        typeConstraint.callExpression.type,
                        actualType
                    )
                }
            }
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
}
