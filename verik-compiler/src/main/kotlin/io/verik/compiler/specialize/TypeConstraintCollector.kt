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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core

object TypeConstraintCollector {

    fun collect(expression: EExpression): List<TypeConstraint> {
        val typeConstraintCollectorVisitor = TypeConstraintCollectorVisitor()
        expression.accept(typeConstraintCollectorVisitor)
        return typeConstraintCollectorVisitor.typeConstraints
    }

    class TypeConstraintCollectorVisitor : TreeVisitor() {

        val typeConstraints = ArrayList<TypeConstraint>()

        override fun visitKtProperty(property: EKtProperty) {
            super.visitKtProperty(property)
            val initializer = property.initializer
            if (initializer != null)
                typeConstraints.add(ExpressionEqualsTypeConstraint(initializer, property))
        }

        override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
            super.visitKtBinaryExpression(binaryExpression)
            if (binaryExpression.kind == KtBinaryOperatorKind.EQ)
                typeConstraints.add(ExpressionEqualsTypeConstraint(binaryExpression.right, binaryExpression.left))
        }

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EExpression)
                typeConstraints.add(ExpressionEqualsTypeConstraint(referenceExpression, reference))
        }

        // TODO general handling of call expressions
        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.U_INT)
                typeConstraints.add(TypeParameterTypeConstraint(callExpression))
        }
    }
}
