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

import io.verik.compiler.core.common.CoreCardinalConstantDeclaration
import java.lang.Integer.max

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
                is TypeParameterTypeConstraint ->
                    if (!resolveTypeParameterTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is ExpressionEqualsTypeConstraint ->
                    if (!resolveExpressionEqualsTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is MaxBitWidthTypeConstraint ->
                    if (!resolveMaxBitWidthTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
            }
        }
        return unresolvedTypeConstraints
    }

    private fun resolveTypeParameterTypeConstraint(typeConstraint: TypeParameterTypeConstraint): Boolean {
        val expressionResolved = typeConstraint.callExpression.type.isResolved()
        val typeParameterResolved = typeConstraint.callExpression.typeArguments[0].isResolved()
        return if (expressionResolved) {
            if (typeParameterResolved) {
                true
            } else {
                typeConstraint.callExpression.typeArguments[0] = typeConstraint.callExpression.type.arguments[0].copy()
                true
            }
        } else {
            if (typeParameterResolved) {
                typeConstraint.callExpression.type.arguments[0] = typeConstraint.callExpression.typeArguments[0].copy()
                true
            } else {
                false
            }
        }
    }

    private fun resolveExpressionEqualsTypeConstraint(typeConstraint: ExpressionEqualsTypeConstraint): Boolean {
        val outerResolved = typeConstraint.outer.type.isResolved()
        val innerResolved = typeConstraint.inner.type.isResolved()
        return if (outerResolved) {
            if (innerResolved) {
                true
            } else {
                typeConstraint.inner.type = typeConstraint.outer.type.copy()
                true
            }
        } else {
            if (innerResolved) {
                typeConstraint.outer.type = typeConstraint.inner.type.copy()
                true
            } else {
                false
            }
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
                val leftWidth = typeConstraint.left.type.asBitWidth(typeConstraint.left)
                val rightWidth = typeConstraint.right.type.asBitWidth(typeConstraint.right)
                typeConstraint.outer.type.arguments[0] =
                    CoreCardinalConstantDeclaration(max(leftWidth, rightWidth)).toType()
                true
            } else {
                false
            }
        }
    }
}
