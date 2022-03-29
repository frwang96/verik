/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.core.common.CoreFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

/**
 * Collector that collects all of the type constraints from the AST.
 */
object TypeConstraintCollector {

    fun collect(projectContext: ProjectContext): List<TypeConstraint> {
        val typeConstraintCollectorVisitor = TypeConstraintCollectorVisitor()
        projectContext.project.accept(typeConstraintCollectorVisitor)
        return typeConstraintCollectorVisitor.typeConstraints
    }

    fun collect(receiverExpression: EReceiverExpression): List<TypeConstraint> {
        return when (receiverExpression) {
            is ECallExpression -> collectCallExpression(receiverExpression)
            is EReferenceExpression -> collectReferenceExpression(receiverExpression)
            else -> Messages.INTERNAL_ERROR.on(receiverExpression, "Call expression or reference expression expected")
        }
    }

    private fun collectCallExpression(callExpression: ECallExpression): List<TypeConstraint> {
        val reference = callExpression.reference
        if (reference !is EDeclaration)
            return listOf()
        if (reference !is EKtAbstractFunction) {
            Messages.INTERNAL_ERROR.on(callExpression, "Expected function as reference")
        }
        val returnTypeConstraint = TypeConstraint(
            TypeConstraintKind.EQ_OUT,
            TypeAdapter.ofElement(callExpression),
            TypeAdapter.ofElement(reference)
        )
        val expectedArgumentSize = reference.valueParameters.size
        val actualArgumentSize = callExpression.valueArguments.size
        if (expectedArgumentSize != actualArgumentSize) {
            Messages.INTERNAL_ERROR.on(
                callExpression,
                "Expected $expectedArgumentSize arguments but found $actualArgumentSize"
            )
        }
        val argumentTypeConstraints = callExpression.valueArguments
            .zip(reference.valueParameters)
            .map { (valueArgument, valueParameter) ->
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(valueArgument),
                    TypeAdapter.ofElement(valueParameter)
                )
            }
        return listOf(returnTypeConstraint) + argumentTypeConstraints
    }

    private fun collectReferenceExpression(referenceExpression: EReferenceExpression): List<TypeConstraint> {
        val reference = referenceExpression.reference
        if (reference !is EDeclaration)
            return listOf()
        if (reference !is EAbstractProperty) {
            Messages.INTERNAL_ERROR.on(reference, "Expected property as reference")
        }
        var parent: EElement? = referenceExpression
        var isLeftOfAssignment = false
        while (parent is EExpression) {
            val parentParent = parent.parent
            if (parentParent is EKtBinaryExpression &&
                parentParent.kind == KtBinaryOperatorKind.EQ &&
                parentParent.left == parent
            ) isLeftOfAssignment = true
            parent = parentParent
        }
        return listOf(
            TypeConstraint(
                if (isLeftOfAssignment) TypeConstraintKind.EQ_IN else TypeConstraintKind.EQ_OUT,
                TypeAdapter.ofElement(referenceExpression),
                TypeAdapter.ofElement(reference)
            )
        )
    }

    private class TypeConstraintCollectorVisitor : TreeVisitor() {

        val typeConstraints = ArrayList<TypeConstraint>()

        private var function: EKtFunction? = null

        override fun visitKtFunction(function: EKtFunction) {
            this.function = function
            super.visitKtFunction(function)
            this.function = null
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
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

        override fun visitEnumEntry(enumEntry: EEnumEntry) {
            super.visitEnumEntry(enumEntry)
            val expression = enumEntry.expression
            val cls = enumEntry.getParentClassOrNull() as EKtClass
            val valueParameter = cls.primaryConstructor?.valueParameters?.firstOrNull()
            if (expression != null && valueParameter != null) {
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_IN,
                        TypeAdapter.ofElement(expression),
                        TypeAdapter.ofElement(valueParameter)
                    )
                )
            }
        }

        override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
            super.visitKtValueParameter(valueParameter)
            val expression = valueParameter.expression
            if (expression != null) {
                typeConstraints.add(
                    TypeConstraint(
                        TypeConstraintKind.EQ_INOUT,
                        TypeAdapter.ofElement(valueParameter),
                        TypeAdapter.ofElement(expression)
                    )
                )
            }
        }

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            super.visitBlockExpression(blockExpression)
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
                KtBinaryOperatorKind.LT,
                KtBinaryOperatorKind.LTEQ,
                KtBinaryOperatorKind.GT,
                KtBinaryOperatorKind.GTEQ,
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

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            when (val reference = callExpression.reference) {
                is CoreFunctionDeclaration -> {
                    typeConstraints.addAll(reference.getTypeConstraints(callExpression))
                }
                is CoreConstructorDeclaration -> {
                    callExpression.typeArguments.indices.forEach {
                        val typeConstraint = TypeConstraint(
                            TypeConstraintKind.EQ_INOUT,
                            TypeAdapter.ofElement(callExpression, it),
                            TypeAdapter.ofTypeArgument(callExpression, it)
                        )
                        typeConstraints.add(typeConstraint)
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
