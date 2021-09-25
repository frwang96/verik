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
                is TypeEqualsTypeConstraint ->
                    checkTypeEqualsTypeConstraint(it)
                is BinaryOperatorTypeConstraint ->
                    checkBinaryOperatorTypeConstraint(it)
                is CardinalBitConstantTypeConstraint ->
                    checkCardinalBitConstantTypeConstraint(it)
                is ConcatenationTypeConstraint ->
                    checkConcatenationTypeConstraint(it)
            }
        }
    }

    private fun checkTypeEqualsTypeConstraint(typeConstraint: TypeEqualsTypeConstraint) {
        val innerType = typeConstraint.inner.getType()
        val outerType = typeConstraint.outer.getType()
        if (outerType != innerType)
            Messages.TYPE_MISMATCH.on(typeConstraint.inner.getLocation(), outerType, innerType)
    }

    private fun checkBinaryOperatorTypeConstraint(typeConstraint: BinaryOperatorTypeConstraint) {
        val leftWidth = typeConstraint.left.type.asBitWidth(typeConstraint.left)
        val rightWidth = typeConstraint.right.type.asBitWidth(typeConstraint.right)
        val innerWidth = when (typeConstraint.kind) {
            BinaryOperatorTypeConstraintKind.MAX -> max(leftWidth, rightWidth)
            BinaryOperatorTypeConstraintKind.MAX_INC -> max(leftWidth, rightWidth) + 1
            BinaryOperatorTypeConstraintKind.ADD -> leftWidth + rightWidth
        }
        val outerWidth = typeConstraint.outer.type.asBitWidth(typeConstraint.outer)
        if (outerWidth != innerWidth) {
            val actualType = typeConstraint.outer.type.copy()
            actualType.arguments[0] = Core.Vk.cardinalOf(innerWidth).toType()
            Messages.TYPE_MISMATCH.on(typeConstraint.outer, typeConstraint.outer.type, actualType)
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
            val actualType = typeConstraint.callExpression.type.copy()
            actualType.arguments[0] = Core.Vk.cardinalOf(typeArgumentValueWidth).toType()
            Messages.TYPE_MISMATCH.on(typeConstraint.callExpression, typeConstraint.callExpression.type, actualType)
        }
    }

    private fun checkConcatenationTypeConstraint(typeConstraint: ConcatenationTypeConstraint) {
        val expressionWidth = typeConstraint.callExpression.type.asBitWidth(typeConstraint.callExpression)
        val valueArgumentWidths = typeConstraint
            .callExpression
            .valueArguments
            .map {
                when (it.type.reference) {
                    Core.Kt.C_Boolean -> 1
                    Core.Vk.C_Ubit -> it.type.asBitWidth(it)
                    Core.Vk.C_Sbit -> it.type.asBitWidth(it)
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
