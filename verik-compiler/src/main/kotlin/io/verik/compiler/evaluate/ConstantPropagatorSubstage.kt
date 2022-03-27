/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.specialize.SpecializerSubstage
import io.verik.compiler.specialize.TypeParameterBinding

object ConstantPropagatorSubstage : SpecializerSubstage() {

    override fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding) {
        val typeParameters = if (typeParameterBinding.declaration is TypeParameterized) {
            typeParameterBinding.declaration.typeParameters
        } else listOf()
        val constantPropagatorVisitor = ConstantPropagatorVisitor(typeParameters)
        declaration.accept(constantPropagatorVisitor)
    }

    private class ConstantPropagatorVisitor(
        private val typeParameters: List<ETypeParameter>
    ) : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EProperty && !reference.isMutable) {
                val initializer = reference.initializer
                if (initializer != null) {
                    val copiedInitializer = ExpressionCopier.deepCopy(initializer, referenceExpression.location)
                    val expandedInitializer = ConstantPropagator.expand(copiedInitializer)
                    val typeParameterCheckerVisitor = TypeParameterCheckerVisitor(typeParameters)
                    expandedInitializer.accept(typeParameterCheckerVisitor)
                    if (typeParameterCheckerVisitor.isAbleToBind &&
                        ConstantPropagator.isConstant(expandedInitializer)
                    ) {
                        referenceExpression.replace(expandedInitializer)
                    }
                }
            }
        }
    }

    private class TypeParameterCheckerVisitor(
        private val typeParameters: List<ETypeParameter>
    ) : TreeVisitor() {

        var isAbleToBind = true

        private fun checkType(type: Type) {
            type.arguments.forEach { checkType(it) }
            val reference = type.reference
            if (reference is ETypeParameter && reference !in typeParameters) {
                isAbleToBind = false
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            checkType(typedElement.type)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments.forEach { checkType(it) }
        }
    }
}
