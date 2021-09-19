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
                is MaxBitWidthTypeConstraint ->
                    if (!resolveMaxBitWidthTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is CardinalBitConstantTypeConstraint ->
                    if (!resolveCardinalBitConstantTypeConstraint(it))
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

    private fun resolveMaxBitWidthTypeConstraint(typeConstraint: MaxBitWidthTypeConstraint): Boolean {
        val leftResolved = typeConstraint.left.type.arguments[0].isResolved()
        val rightResolved = typeConstraint.right.type.arguments[0].isResolved()
        val outerResolved = typeConstraint.outer.type.arguments[0].isResolved()
        return if (outerResolved) {
            true
        } else {
            if (leftResolved && rightResolved) {
                val leftType = typeConstraint.left.type.arguments[0].copy()
                val rightType = typeConstraint.right.type.arguments[0].copy()
                typeConstraint.outer.type.arguments[0] = Core.Vk.N_MAX.toType(leftType, rightType)
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
}
