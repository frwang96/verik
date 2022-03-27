/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CardinalConstantDeclaration
import io.verik.compiler.message.Messages

object TypeParameterSubstitutorSubstage : SpecializerSubstage() {

    override fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding) {
        val typeParameters = if (typeParameterBinding.declaration is TypeParameterized) {
            typeParameterBinding.declaration.typeParameters
        } else listOf()
        val expectedSize = typeParameters.size
        val actualSize = typeParameterBinding.typeArguments.size
        if (expectedSize != actualSize) {
            Messages.INTERNAL_ERROR.on(
                declaration,
                "Mismatched type parameters: Expected $expectedSize but found $actualSize"
            )
        }
        val typeParameterSubstitutorVisitor =
            TypeParameterSubstitutorVisitor(typeParameters, typeParameterBinding.typeArguments)
        declaration.accept(typeParameterSubstitutorVisitor)

        if (typeParameters.isNotEmpty() && !typeParameterBinding.declaration.isImported()) {
            val typeParameterStrings = typeParameters
                .zip(typeParameterBinding.typeArguments)
                .map { (typeParameter, typeArgument) -> getTypeArgumentString(typeParameter, typeArgument) }
            val name = "${declaration.name}_${typeParameterStrings.joinToString(separator = "_")}"
            declaration.name = name
            if (declaration is EKtClass) {
                declaration.primaryConstructor?.name = name
            }
        }
    }

    private fun getTypeArgumentString(typeParameter: ETypeParameter, typeArgument: Type): String {
        val reference = typeArgument.reference
        return if (reference is CardinalConstantDeclaration) {
            "${typeParameter.name}_${reference.value}"
        } else {
            "${typeParameter.name}_${reference.name}"
        }
    }

    private class TypeParameterSubstitutorVisitor(
        private val typeParameters: List<ETypeParameter>,
        private val typeArguments: List<Type>
    ) : TreeVisitor() {

        private fun substitute(type: Type, element: EElement): Type {
            val reference = type.reference
            return if (reference is ETypeParameter) {
                val index = typeParameters.indexOf(reference)
                if (index == -1) {
                    Messages.INTERNAL_ERROR.on(element, "Unable to substitute type parameter: ${reference.name}")
                }
                typeArguments[index].copy()
            } else {
                val arguments = type.arguments.map { substitute(it, element) }
                reference.toType(arguments)
            }
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            typedElement.type = substitute(typedElement.type, typedElement)
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.superType = substitute(cls.superType, cls)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments = ArrayList(
                callExpression.typeArguments.map { substitute(it, callExpression) }
            )
        }
    }
}
