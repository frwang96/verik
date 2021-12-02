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

import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CardinalDeclaration
import io.verik.compiler.core.common.CoreFunctionDeclaration
import io.verik.compiler.message.Messages

object TypeConstraintCollector {

    fun collect(function: EKtFunction): List<TypeConstraint> {
        val typeConstraintCollectorVisitor = TypeConstraintCollectorVisitor()
        function.accept(typeConstraintCollectorVisitor)
        return typeConstraintCollectorVisitor.typeConstraints
    }

    fun collect(property: EKtProperty): List<TypeConstraint> {
        val typeConstraintCollectorVisitor = TypeConstraintCollectorVisitor()
        property.accept(typeConstraintCollectorVisitor)
        return typeConstraintCollectorVisitor.typeConstraints
    }

    private class TypeConstraintCollectorVisitor : TreeVisitor() {

        val typeConstraints = ArrayList<TypeConstraint>()

        private var function: EKtFunction? = null

        override fun visitKtFunction(function: EKtFunction) {
            this.function = function
            super.visitKtFunction(function)
            this.function = null
        }

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            val initializer = property.initializer
            if (initializer != null)
                collectTypeEquals(initializer, property)
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            if (blockExpression.statements.isNotEmpty()) {
                val statement = blockExpression.statements.last()
                collectTypeEquals(statement, blockExpression)
            }
        }

        override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
            super.visitKtUnaryExpression(unaryExpression)
            val kinds = listOf(
                KtUnaryOperatorKind.PRE_INC,
                KtUnaryOperatorKind.PRE_DEC,
                KtUnaryOperatorKind.POST_INC,
                KtUnaryOperatorKind.POST_DEC
            )
            if (unaryExpression.kind in kinds)
                collectTypeEquals(unaryExpression.expression, unaryExpression)
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val kinds = listOf(
                KtBinaryOperatorKind.EQ,
                KtBinaryOperatorKind.EQEQ,
                KtBinaryOperatorKind.EXCL_EQ
            )
            if (binaryExpression.kind in kinds)
                collectTypeEquals(binaryExpression.right, binaryExpression.left)
        }

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            when (val reference = referenceExpression.reference) {
                is EExpression -> collectTypeEquals(referenceExpression, reference)
                is EAbstractProperty -> collectTypeEquals(referenceExpression, reference)
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            when (val reference = callExpression.reference) {
                is CoreFunctionDeclaration ->
                    typeConstraints.addAll(reference.getTypeConstraints(callExpression))
                is EKtAbstractFunction -> {
                    collectCallExpressionReturn(callExpression, reference, listOf())
                    callExpression.valueArguments.indices.forEach {
                        collectCallExpressionValueArgument(callExpression, reference, it, listOf())
                    }
                }
            }
        }

        override fun visitReturnStatement(returnStatement: EReturnStatement) {
            super.visitReturnStatement(returnStatement)
            val expression = returnStatement.expression
            if (expression != null) {
                val function = this.function
                if (function != null) {
                    collectTypeEquals(expression, function)
                } else {
                    Messages.INTERNAL_ERROR.on(returnStatement, "Could not identify return type")
                }
            }
        }

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            val thenExpression = ifExpression.thenExpression
            val elseExpression = ifExpression.elseExpression
            if (thenExpression != null && elseExpression != null) {
                collectTypeEquals(thenExpression, ifExpression)
                collectTypeEquals(elseExpression, ifExpression)
            }
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            whenExpression.entries.forEach {
                collectTypeEquals(it.body, whenExpression)
            }
        }

        private fun collectCallExpressionReturn(
            callExpression: EKtCallExpression,
            function: EKtAbstractFunction,
            typeArgumentIndices: List<Int>
        ) {
            val type = function.type.getArgument(typeArgumentIndices)
            when (val reference = type.reference) {
                is CardinalDeclaration -> {
                    typeConstraints.add(
                        TypeEqualsTypeConstraint(
                            TypeAdapter.ofElement(callExpression, typeArgumentIndices),
                            TypeAdapter.ofElement(function, typeArgumentIndices)
                        )
                    )
                }
                is ETypeParameter -> {
                    val typeParameterIndex = function.typeParameters.indexOf(reference)
                    if (typeParameterIndex != -1) {
                        typeConstraints.add(
                            TypeEqualsTypeConstraint(
                                TypeAdapter.ofElement(callExpression, typeArgumentIndices),
                                TypeAdapter.ofTypeArgument(callExpression, typeParameterIndex)
                            )
                        )
                    }
                }
                else -> {
                    type.arguments.indices.forEach {
                        collectCallExpressionReturn(callExpression, function, typeArgumentIndices + it)
                    }
                }
            }
        }

        private fun collectCallExpressionValueArgument(
            callExpression: EKtCallExpression,
            function: EKtAbstractFunction,
            valueArgumentIndex: Int,
            typeArgumentIndices: List<Int>
        ) {
            val valueArgument = callExpression.valueArguments[valueArgumentIndex]
            val valueParameter = function.valueParameters[valueArgumentIndex]
            val type = valueParameter.type.getArgument(typeArgumentIndices)
            when (val reference = type.reference) {
                is CardinalDeclaration -> {
                    typeConstraints.add(
                        TypeEqualsTypeConstraint(
                            TypeAdapter.ofElement(valueArgument, typeArgumentIndices),
                            TypeAdapter.ofElement(valueParameter, typeArgumentIndices)
                        )
                    )
                }
                is ETypeParameter -> {
                    val typeParameterIndex = function.typeParameters.indexOf(reference)
                    if (typeParameterIndex != -1) {
                        typeConstraints.add(
                            TypeEqualsTypeConstraint(
                                TypeAdapter.ofElement(valueArgument, typeArgumentIndices),
                                TypeAdapter.ofTypeArgument(callExpression, typeParameterIndex)
                            )
                        )
                    }
                }
                else -> {
                    type.arguments.indices.forEach {
                        collectCallExpressionValueArgument(
                            callExpression,
                            function,
                            valueArgumentIndex,
                            typeArgumentIndices + it
                        )
                    }
                }
            }
        }

        private fun collectTypeEquals(
            inner: ETypedElement,
            outer: ETypedElement,
            indices: List<Int> = listOf()
        ) {
            val innerType = inner.type.getArgument(indices)
            val outerType = outer.type.getArgument(indices)
            if (innerType.isCardinalType() && outerType.isCardinalType()) {
                typeConstraints.add(
                    TypeEqualsTypeConstraint(
                        TypeAdapter.ofElement(inner, indices),
                        TypeAdapter.ofElement(outer, indices)
                    )
                )
                return
            }
            val innerTypeReference = innerType.reference
            val outerTypeReference = outerType.reference
            if (innerTypeReference == outerTypeReference) {
                innerType.arguments.indices.forEach {
                    collectTypeEquals(inner, outer, indices + it)
                }
            }
        }
    }
}
