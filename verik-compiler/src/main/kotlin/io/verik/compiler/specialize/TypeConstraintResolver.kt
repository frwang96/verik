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

object TypeConstraintResolver {

    fun resolve(typeConstraints: List<TypeConstraint>) {
        var unresolvedCount = typeConstraints.size
        var unresolvedTypeConstraints = resolveTypeConstraints(typeConstraints)
        while (unresolvedTypeConstraints.isNotEmpty() && unresolvedTypeConstraints.size < unresolvedCount) {
            unresolvedCount = unresolvedTypeConstraints.size
            unresolvedTypeConstraints = resolveTypeConstraints(unresolvedTypeConstraints)
        }
    }

    private fun resolveTypeConstraints(typeConstraints: List<TypeConstraint>): List<TypeConstraint> {
        val unresolvedTypeConstraints = ArrayList<TypeConstraint>()
        typeConstraints.forEach {
            when (it) {
                is TypeArgumentTypeConstraint ->
                    if (!resolveTypeArgumentTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is ValueArgumentTypeConstraint ->
                    if (!resolveValueArgumentTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is TypeEqualsTypeConstraint ->
                    if (!resolveTypeEqualsTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is BinaryOperatorTypeConstraint ->
                    if (!resolveBinaryOperatorTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is CardinalBitConstantTypeConstraint ->
                    if (!resolveCardinalBitConstantTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is ConcatenationTypeConstraint ->
                    if (!resolveConcatenationTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
            }
        }
        return unresolvedTypeConstraints
    }

    private fun resolveTypeArgumentTypeConstraint(typeConstraint: TypeArgumentTypeConstraint): Boolean {
        val expressionResolved = typeConstraint.callExpression.type.isResolved()
        val typeArgumentResolved = typeConstraint.callExpression.typeArguments[0].isResolved()
        return if (expressionResolved) {
            if (typeArgumentResolved) {
                true
            } else {
                typeConstraint.callExpression.typeArguments[0] = typeConstraint.getTypeArgument().copy()
                true
            }
        } else {
            if (typeArgumentResolved) {
                typeConstraint.setTypeArgument(typeConstraint.callExpression.typeArguments[0].copy())
                true
            } else {
                false
            }
        }
    }

    private fun resolveValueArgumentTypeConstraint(typeConstraint: ValueArgumentTypeConstraint): Boolean {
        if (!typeConstraint.valueArgument.type.isResolved())
            typeConstraint.valueArgument.type = typeConstraint.valueParameter.type.copy()
        return true
    }

    private fun resolveTypeEqualsTypeConstraint(typeConstraint: TypeEqualsTypeConstraint): Boolean {
        val innerResolved = typeConstraint.inner.type.isResolved()
        val outerResolved = typeConstraint.outer.type.isResolved()
        return if (outerResolved) {
            if (!innerResolved)
                typeConstraint.inner.type = typeConstraint.outer.type.copy()
            true
        } else {
            if (innerResolved) {
                typeConstraint.outer.type = typeConstraint.inner.type.copy()
                true
            } else false
        }
    }

    private fun resolveBinaryOperatorTypeConstraint(typeConstraint: BinaryOperatorTypeConstraint): Boolean {
        val leftResolved = typeConstraint.left.type.arguments[0].isResolved()
        val rightResolved = typeConstraint.right.type.arguments[0].isResolved()
        val outerResolved = typeConstraint.outer.type.arguments[0].isResolved()
        return if (outerResolved) {
            true
        } else {
            if (leftResolved && rightResolved) {
                val leftType = typeConstraint.left.type.arguments[0].copy()
                val rightType = typeConstraint.right.type.arguments[0].copy()
                val type = when (typeConstraint.kind) {
                    BinaryOperatorTypeConstraintKind.MAX ->
                        Core.Vk.N_MAX.toType(leftType, rightType)
                    BinaryOperatorTypeConstraintKind.MAX_INC ->
                        Core.Vk.N_INC.toType(Core.Vk.N_MAX.toType(leftType, rightType))
                    BinaryOperatorTypeConstraintKind.ADD ->
                        Core.Vk.N_ADD.toType(leftType, rightType)
                }
                typeConstraint.outer.type.arguments[0] = type
                true
            } else {
                false
            }
        }
    }

    private fun resolveCardinalBitConstantTypeConstraint(typeConstraint: CardinalBitConstantTypeConstraint): Boolean {
        val expressionResolved = typeConstraint.callExpression.type.isResolved()
        val typeArgumentResolved = typeConstraint.callExpression.typeArguments[0].isResolved()
        return if (expressionResolved) {
            true
        } else {
            if (typeArgumentResolved) {
                val type = typeConstraint.callExpression.typeArguments[0].copy()
                typeConstraint.callExpression.type.arguments[0] = Core.Vk.N_INCLOG.toType(type)
                true
            } else false
        }
    }

    private fun resolveConcatenationTypeConstraint(typeConstraint: ConcatenationTypeConstraint): Boolean {
        val expressionResolved = typeConstraint.callExpression.type.isResolved()
        val valueArgumentsResolved = typeConstraint.callExpression.valueArguments.all { it.type.isResolved() }
        return if (expressionResolved) {
            true
        } else {
            if (valueArgumentsResolved) {
                val cardinalTypes = typeConstraint.callExpression.valueArguments.map {
                    when (it.type.reference) {
                        Core.Kt.C_BOOLEAN -> Core.Vk.cardinalOf(1).toType()
                        Core.Vk.C_UBIT -> it.type.arguments[0].copy()
                        Core.Vk.C_SBIT -> it.type.arguments[0].copy()
                        else -> {
                            Messages.TYPE_NO_WIDTH.on(it, it.type)
                            Core.Vk.cardinalOf(0).toType()
                        }
                    }
                }
                val type = when (cardinalTypes.size) {
                    0 -> Core.Vk.cardinalOf(0).toType()
                    1 -> cardinalTypes[0]
                    else -> cardinalTypes.reduce { sum, type ->
                        Core.Vk.N_ADD.toType(sum, type)
                    }
                }
                typeConstraint.callExpression.type.arguments[0] = type
                true
            } else false
        }
    }
}
