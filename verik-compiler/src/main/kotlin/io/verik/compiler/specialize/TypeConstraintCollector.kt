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

import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtAbstractFunctionDeclaration

object TypeConstraintCollector {

    fun collect(expression: EExpression): List<TypeConstraint> {
        val typeConstraintCollectorVisitor = TypeConstraintCollectorVisitor()
        expression.accept(typeConstraintCollectorVisitor)
        return typeConstraintCollectorVisitor.typeConstraints
    }

    fun collect(property: EKtProperty): List<TypeConstraint> {
        val typeConstraintCollectorVisitor = TypeConstraintCollectorVisitor()
        property.accept(typeConstraintCollectorVisitor)
        return typeConstraintCollectorVisitor.typeConstraints
    }

    class TypeConstraintCollectorVisitor : TreeVisitor() {

        val typeConstraints = ArrayList<TypeConstraint>()

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            val initializer = property.initializer
            if (initializer != null)
                typeConstraints.add(
                    TypeEqualsTypeConstraint(TypeAdapter.ofElement(initializer), TypeAdapter.ofElement(property))
                )
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            if (blockExpression.type !in listOf(Core.Kt.C_Unit.toType(), Core.Kt.C_Function.toType())) {
                if (blockExpression.statements.isNotEmpty()) {
                    val statement = blockExpression.statements.last()
                    typeConstraints.add(
                        TypeEqualsTypeConstraint(
                            TypeAdapter.ofElement(statement),
                            TypeAdapter.ofElement(blockExpression)
                        )
                    )
                }
            }
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            val kinds = listOf(
                KtBinaryOperatorKind.EQ,
                KtBinaryOperatorKind.EQEQ,
                KtBinaryOperatorKind.EXCL_EQ
            )
            if (binaryExpression.kind in kinds) {
                typeConstraints.add(
                    TypeEqualsTypeConstraint(
                        TypeAdapter.ofElement(binaryExpression.right),
                        TypeAdapter.ofElement(binaryExpression.left)
                    )
                )
            }
        }

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            when (val reference = referenceExpression.reference) {
                is EExpression ->
                    typeConstraints.add(
                        TypeEqualsTypeConstraint(
                            TypeAdapter.ofElement(referenceExpression),
                            TypeAdapter.ofElement(reference)
                        )
                    )
                is EAbstractProperty ->
                    typeConstraints.add(
                        TypeEqualsTypeConstraint(
                            TypeAdapter.ofElement(referenceExpression),
                            TypeAdapter.ofElement(reference)
                        )
                    )
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            when (val reference = callExpression.reference) {
                is CoreKtAbstractFunctionDeclaration ->
                    typeConstraints.addAll(reference.getTypeConstraints(callExpression))
                is EKtAbstractFunction -> {
                    callExpression.valueArguments
                        .zip(reference.valueParameters)
                        .forEach { (valueArgument, valueParameter) ->
                            typeConstraints.add(
                                TypeEqualsTypeConstraint(
                                    TypeAdapter.ofElement(valueArgument),
                                    TypeAdapter.ofElement(valueParameter)
                                )
                            )
                        }
                }
            }
        }

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            val thenExpression = ifExpression.thenExpression
            val elseExpression = ifExpression.elseExpression
            if (thenExpression != null && elseExpression != null) {
                typeConstraints.add(
                    TypeEqualsTypeConstraint(
                        TypeAdapter.ofElement(ifExpression),
                        TypeAdapter.ofElement(thenExpression)
                    )
                )
                typeConstraints.add(
                    TypeEqualsTypeConstraint(
                        TypeAdapter.ofElement(ifExpression),
                        TypeAdapter.ofElement(elseExpression)
                    )
                )
            }
        }
    }
}
