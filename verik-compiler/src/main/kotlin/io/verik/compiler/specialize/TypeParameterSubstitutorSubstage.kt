/*
 * Copyright (c) 2022 Francis Wang
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

        if (typeParameters.isNotEmpty()) {
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

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            `class`.superType = substitute(`class`.superType, `class`)
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            callExpression.typeArguments = ArrayList(
                callExpression.typeArguments.map { substitute(it, callExpression) }
            )
        }
    }
}
