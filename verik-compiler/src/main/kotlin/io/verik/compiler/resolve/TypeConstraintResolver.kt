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
                is EqualsTypeConstraint ->
                    if (!resolveEqualsTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is UnaryTypeConstraint ->
                    if (!resolveUnaryTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is BinaryTypeConstraint ->
                    if (!resolveBinaryTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is SpecialTypeConstraint ->
                    if (!resolveSpecialTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is ComparisonTypeConstraint -> {}
            }
        }
        return unresolvedTypeConstraints
    }

    private fun resolveEqualsTypeConstraint(typeConstraint: EqualsTypeConstraint): Boolean {
        val inner = typeConstraint.inner.getType()
        val outer = typeConstraint.outer.getType()
        val innerResolved = inner.isResolved()
        val outerResolved = outer.isResolved()
        return if (outerResolved) {
            if (!innerResolved)
                typeConstraint.inner.setType(outer.copy())
            true
        } else {
            if (innerResolved) {
                typeConstraint.outer.setType(inner.copy())
                true
            } else false
        }
    }

    private fun resolveUnaryTypeConstraint(typeConstraint: UnaryTypeConstraint): Boolean {
        val inner = typeConstraint.inner.getType()
        val outer = typeConstraint.outer.getType()
        val innerResolved = inner.isResolved()
        val outerResolved = outer.isResolved()
        return if (typeConstraint.isInnerToOuter) {
            if (outerResolved) {
                true
            } else {
                if (innerResolved) {
                    typeConstraint.outer.setType(typeConstraint.kind.resolve(inner))
                    true
                } else false
            }
        } else {
            if (innerResolved) {
                true
            } else {
                if (outerResolved) {
                    typeConstraint.inner.setType(typeConstraint.kind.resolve(outer))
                    true
                } else false
            }
        }
    }

    private fun resolveBinaryTypeConstraint(typeConstraint: BinaryTypeConstraint): Boolean {
        val left = typeConstraint.left.getType()
        val right = typeConstraint.right.getType()
        val outer = typeConstraint.outer.getType()
        val leftResolved = left.isResolved()
        val rightResolved = right.isResolved()
        val outerResolved = outer.isResolved()
        return if (outerResolved) {
            true
        } else {
            if (leftResolved && rightResolved) {
                typeConstraint.outer.setType(typeConstraint.kind.resolve(left, right))
                true
            } else {
                false
            }
        }
    }

    private fun resolveSpecialTypeConstraint(typeConstraint: SpecialTypeConstraint): Boolean {
        val expressionType = typeConstraint.callExpression.type
        val valueArgumentsType = typeConstraint.callExpression.valueArguments.map { it.type }
        val expressionResolved = expressionType.isResolved()
        val valueArgumentsResolved = valueArgumentsType.all { it.isResolved() }
        return if (expressionResolved) {
            true
        } else {
            if (valueArgumentsResolved) {
                val widths = typeConstraint.callExpression.valueArguments
                    .map { it.type.getWidthAsType(it) }
                when (typeConstraint.kind) {
                    SpecialTypeConstraintKind.CONSTANT_ONE -> {
                        typeConstraint.callExpression.type.arguments[0] = Cardinal.of(1).toType()
                    }
                    SpecialTypeConstraintKind.CAT -> {
                        val type = when (widths.size) {
                            0 -> Cardinal.of(0).toType()
                            1 -> widths[0]
                            else -> widths.reduce { sum, type ->
                                Core.Vk.T_ADD.toType(sum, type)
                            }
                        }
                        typeConstraint.callExpression.type.arguments[0] = type
                    }
                    SpecialTypeConstraintKind.REP -> {
                        val typeArgument = typeConstraint.callExpression.typeArguments[0]
                        val type = Core.Vk.T_MUL.toType(widths[0], typeArgument.copy())
                        typeConstraint.callExpression.type.arguments[0] = type
                    }
                }
                true
            } else false
        }
    }
}
