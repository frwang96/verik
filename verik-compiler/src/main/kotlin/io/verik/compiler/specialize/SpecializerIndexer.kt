/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.cast
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor

object SpecializerIndexer {

    fun index(declaration: EDeclaration): List<TypeParameterBinding> {
        val specializerIndexerVisitor = SpecializerIndexerVisitor()
        declaration.accept(specializerIndexerVisitor)
        return specializerIndexerVisitor.typeParameterBindings
    }

    private class SpecializerIndexerVisitor : TreeVisitor() {

        val typeParameterBindings = ArrayList<TypeParameterBinding>()

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            addType(typedElement.type)
            when (typedElement) {
                is EAbstractClass -> addType(typedElement.superType)
                is ECallExpression -> typedElement.typeArguments.forEach { addType(it) }
            }
        }

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            if (referenceExpression.receiver != null)
                return
            val reference = referenceExpression.reference
            if (reference is EDeclaration) {
                when (val parent = reference.parent) {
                    is EFile, is ECompanionObject -> {
                        typeParameterBindings.add(TypeParameterBinding(reference, listOf()))
                    }
                    is EKtClass -> {
                        if (parent.isObject || parent.isEnum) {
                            typeParameterBindings.add(TypeParameterBinding(parent, listOf()))
                        }
                    }
                }
            }
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.receiver != null) return
            val reference = callExpression.reference
            if (reference is EPrimaryConstructor || reference is ESecondaryConstructor) {
                if (isTypeArgumentsResolved(callExpression)) {
                    val parent = reference.cast<EDeclaration>(callExpression).parentNotNull().cast<EDeclaration>()
                    typeParameterBindings.add(TypeParameterBinding(parent, callExpression.typeArguments))
                }
            } else if (reference is EKtAbstractFunction) {
                when (val parent = reference.parent) {
                    is EFile, is ECompanionObject -> {
                        if (isTypeArgumentsResolved(callExpression)) {
                            typeParameterBindings.add(TypeParameterBinding(reference, callExpression.typeArguments))
                        }
                    }
                    is EKtClass -> {
                        if (parent.isObject) {
                            typeParameterBindings.add(TypeParameterBinding(parent, listOf()))
                        }
                    }
                }
            }
        }

        private fun addType(type: Type) {
            type.arguments.forEach { addType(it) }
            val reference = type.reference
            if (type.isResolved() && reference is EKtClass) {
                typeParameterBindings.add(TypeParameterBinding(reference, type.arguments))
            }
        }

        private fun isTypeArgumentsResolved(callExpression: ECallExpression): Boolean {
            return callExpression.typeArguments.all { it.isResolved() }
        }
    }
}
