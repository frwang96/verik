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
import java.lang.Integer.max

object TypeConstraintChecker {

    fun check(typeConstraints: List<TypeConstraint>) {
        typeConstraints.forEach {
            when (it) {
                is TypeArgumentTypeConstraint ->
                    checkTypeArgumentTypeConstraint(it)
                is ValueArgumentTypeConstraint ->
                    checkValueArgumentTypeConstraint(it)
                is TypeEqualsTypeConstraint ->
                    checkTypeEqualsTypeConstraint(it)
                is MaxBitWidthTypeConstraint ->
                    checkMaxBitWidthTypeConstraint(it)
                is CardinalBitConstantTypeConstraint ->
                    checkCardinalBitConstantTypeConstraint(it)
                is ConcatenationTypeConstraint ->
                    checkConcatenationTypeConstraint(it)
            }
        }
    }

    private fun checkTypeArgumentTypeConstraint(typeConstraint: TypeArgumentTypeConstraint) {
        if (typeConstraint.getTypeArgument() != typeConstraint.callExpression.typeArguments[0])
            Messages.TYPE_MISMATCH.on(
                typeConstraint.callExpression,
                typeConstraint.getTypeArgument(),
                typeConstraint.callExpression.typeArguments[0]
            )
    }

    private fun checkValueArgumentTypeConstraint(typeConstraint: ValueArgumentTypeConstraint) {
        if (typeConstraint.valueArgument.type != typeConstraint.valueParameter.type)
            Messages.TYPE_MISMATCH.on(
                typeConstraint.valueArgument,
                typeConstraint.valueParameter.type,
                typeConstraint.valueArgument.type
            )
    }

    private fun checkTypeEqualsTypeConstraint(typeConstraint: TypeEqualsTypeConstraint) {
        if (typeConstraint.outer.type != typeConstraint.inner.type)
            Messages.TYPE_MISMATCH.on(typeConstraint.inner, typeConstraint.outer.type, typeConstraint.inner.type)
    }

    private fun checkMaxBitWidthTypeConstraint(typeConstraint: MaxBitWidthTypeConstraint) {
        val leftWidth = typeConstraint.left.type.asBitWidth(typeConstraint.left)
        val rightWidth = typeConstraint.right.type.asBitWidth(typeConstraint.right)
        val outerWidth = typeConstraint.outer.type.asBitWidth(typeConstraint.outer)
        if (outerWidth != max(leftWidth, rightWidth)) {
            val expectedType = typeConstraint.outer.type.copy()
            expectedType.arguments[0] = Core.Vk.cardinalOf(max(leftWidth, rightWidth)).toType()
            Messages.TYPE_MISMATCH.on(typeConstraint.outer, expectedType, typeConstraint.outer.type)
        }
    }

    private fun checkCardinalBitConstantTypeConstraint(typeConstraint: CardinalBitConstantTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type.asBitWidth(typeConstraint.callExpression)
        val typeArgumentValue = typeConstraint
            .callExpression
            .typeArguments[0]
            .asCardinalValue(typeConstraint.callExpression)
        val typeArgumentValueWidth = 32 - typeArgumentValue.countLeadingZeroBits()
        if (typeArgumentValueWidth != expressionWidth) {
            val expectedType = typeConstraint.callExpression.type.copy()
            expectedType.arguments[0] = Core.Vk.cardinalOf(typeArgumentValueWidth).toType()
            Messages.TYPE_MISMATCH.on(typeConstraint.callExpression, expectedType, typeConstraint.callExpression.type)
        }
    }

    private fun checkConcatenationTypeConstraint(typeConstraint: ConcatenationTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type.asBitWidth(typeConstraint.callExpression)
        val valueArgumentWidths = typeConstraint
            .callExpression
            .valueArguments
            .map {
                when (it.type.reference) {
                    Core.Kt.C_BOOLEAN -> 1
                    Core.Vk.C_UBIT -> it.type.asBitWidth(it)
                    Core.Vk.C_SBIT -> it.type.asBitWidth(it)
                    else -> 0
                }
            }
        val sumWidth = valueArgumentWidths.sum()
        if (sumWidth != expressionWidth) {
            val expectedType = typeConstraint.callExpression.type.copy()
            expectedType.arguments[0] = Core.Vk.cardinalOf(sumWidth).toType()
            Messages.TYPE_MISMATCH.on(typeConstraint.callExpression, expectedType, typeConstraint.callExpression.type)
        }
    }
}
