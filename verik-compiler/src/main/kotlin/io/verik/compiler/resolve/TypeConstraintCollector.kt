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
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
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
import io.verik.compiler.core.common.Core
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
            if (initializer != null) {
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(property),
                        TypeAdapter.ofElement(initializer)
                    )
                )
            }
        }

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            if (blockExpression.statements.isNotEmpty() && blockExpression.type.reference != Core.Kt.C_Unit) {
                val statement = blockExpression.statements.last()
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(blockExpression),
                        TypeAdapter.ofElement(statement)
                    )
                )
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
            if (unaryExpression.kind in kinds) {
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(unaryExpression),
                        TypeAdapter.ofElement(unaryExpression.expression)
                    )
                )
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
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(binaryExpression.left),
                        TypeAdapter.ofElement(binaryExpression.right)
                    )
                )
            }
        }

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EAbstractProperty) {
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_OUT,
                        TypeAdapter.ofElement(referenceExpression),
                        TypeAdapter.ofElement(reference)
                    )
                )
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            when (val reference = callExpression.reference) {
                is CoreFunctionDeclaration ->
                    typeConstraints.addAll(reference.getTypeConstraints(callExpression))
                is EKtAbstractFunction -> {
                    // TODO get type constraints after forwarding reference
                }
            }
        }

        override fun visitReturnStatement(returnStatement: EReturnStatement) {
            super.visitReturnStatement(returnStatement)
            val expression = returnStatement.expression
            if (expression != null) {
                val function = this.function
                if (function != null) {
                    typeConstraints.add(
                        TypeConstraint(
                            TypeConstraintKind.EQ_OUT,
                            TypeAdapter.ofElement(expression),
                            TypeAdapter.ofElement(function)
                        )
                    )
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
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(ifExpression),
                        TypeAdapter.ofElement(thenExpression)
                    )
                )
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(ifExpression),
                        TypeAdapter.ofElement(elseExpression)
                    )
                )
            }
        }

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            whenExpression.entries.forEach {
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(whenExpression),
                        TypeAdapter.ofElement(it.body)
                    )
                )
            }
        }
    }
}
