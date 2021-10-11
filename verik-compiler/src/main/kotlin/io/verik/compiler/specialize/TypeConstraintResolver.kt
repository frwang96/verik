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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
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
                is TypeEqualsTypeConstraint ->
                    if (!resolveTypeEqualsTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is UnaryOperatorTypeConstraint ->
                    if (!resolveUnaryOperatorTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is BinaryOperatorTypeConstraint ->
                    if (!resolveBinaryOperatorTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is ConcatenationTypeConstraint ->
                    if (!resolveConcatenationTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
                is ReplicationTypeConstraint ->
                    if (!resolveReplicationTypeConstraint(it))
                        unresolvedTypeConstraints.add(it)
            }
        }
        return unresolvedTypeConstraints
    }

    private fun resolveTypeEqualsTypeConstraint(typeConstraint: TypeEqualsTypeConstraint): Boolean {
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

    private fun resolveUnaryOperatorTypeConstraint(typeConstraint: UnaryOperatorTypeConstraint): Boolean {
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

    private fun resolveBinaryOperatorTypeConstraint(typeConstraint: BinaryOperatorTypeConstraint): Boolean {
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

    private fun resolveConcatenationTypeConstraint(typeConstraint: ConcatenationTypeConstraint): Boolean {
        val expressionResolved = typeConstraint.callExpression.type.isResolved()
        val valueArgumentsResolved = typeConstraint.callExpression.valueArguments.all { it.type.isResolved() }
        return if (expressionResolved) {
            true
        } else {
            if (valueArgumentsResolved) {
                val typeWidths = typeConstraint.callExpression.valueArguments
                    .map { getTypeWidth(it.type, it) }
                val type = when (typeWidths.size) {
                    0 -> Core.Vk.cardinalOf(0).toType()
                    1 -> typeWidths[0]
                    else -> typeWidths.reduce { sum, type ->
                        Core.Vk.N_ADD.toType(sum, type)
                    }
                }
                typeConstraint.callExpression.type.arguments[0] = type
                true
            } else false
        }
    }

    private fun resolveReplicationTypeConstraint(typeConstraint: ReplicationTypeConstraint): Boolean {
        val expressionType = typeConstraint.callExpression.type
        val valueArgumentType = typeConstraint.callExpression.valueArguments[0].type
        val expressionResolved = expressionType.isResolved()
        val valueArgumentResolved = valueArgumentType.isResolved()
        return if (expressionResolved) {
            true
        } else {
            if (valueArgumentResolved) {
                val typeWidth = getTypeWidth(valueArgumentType, typeConstraint.callExpression.valueArguments[0])
                val typeArgument = typeConstraint.callExpression.typeArguments[0]
                val type = Core.Vk.N_MUL.toType(typeWidth, typeArgument.copy())
                typeConstraint.callExpression.type.arguments[0] = type
                true
            } else false
        }
    }

    private fun getTypeWidth(type: Type, element: EElement): Type {
        return when (type.reference) {
            Core.Kt.C_Boolean -> Core.Vk.cardinalOf(1).toType()
            Core.Vk.C_Ubit -> type.arguments[0].copy()
            Core.Vk.C_Sbit -> type.arguments[0].copy()
            else -> {
                Messages.TYPE_NO_WIDTH.on(element, type)
                Core.Vk.cardinalOf(0).toType()
            }
        }
    }
}
