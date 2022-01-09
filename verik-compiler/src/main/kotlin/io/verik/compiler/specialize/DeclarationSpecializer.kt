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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CardinalConstantDeclaration
import io.verik.compiler.message.Messages

object DeclarationSpecializer {

    fun specialize(
        declarationBinding: DeclarationBinding,
        specializeContext: SpecializeContext,
    ): List<DeclarationBinding> {
        val declaration = SpecializerCopier.copy(
            declarationBinding.declaration,
            declarationBinding.typeParameterBindings,
            specializeContext
        )
        val typeParameterSubstitutorVisitor = TypeParameterSubstitutorVisitor(declarationBinding.typeParameterBindings)
        declaration.accept(typeParameterSubstitutorVisitor)
        if (declaration is TypeParameterized && declaration.typeParameters.isNotEmpty()) {
            val typeParameterStrings = declaration.typeParameters.map { getTypeParameterString(it) }
            declaration.name = "${declaration.name}_${typeParameterStrings.joinToString(separator = "_")}"
        }
        return SpecializerIndexer.index(declaration)
    }

    private fun getTypeParameterString(typeParameter: ETypeParameter): String {
        val reference = typeParameter.type.reference
        return if (reference is CardinalConstantDeclaration) {
            "${typeParameter.name}_${reference.value}"
        } else {
            "${typeParameter.name}_${reference.name}"
        }
    }

    private class TypeParameterSubstitutorVisitor(
        private val typeParameterBindings: List<TypeParameterBinding>
    ) : TreeVisitor() {

        private fun substitute(type: Type, element: EElement): Type {
            val reference = type.reference
            return if (reference is ETypeParameter) {
                val typeParameterBinding = typeParameterBindings.find { it.typeParameter == reference }
                    ?: Messages.INTERNAL_ERROR.on(element, "Unable to substitute type parameter: ${reference.name}")
                typeParameterBinding.type.copy()
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

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            callExpression.typeArguments = ArrayList(
                callExpression.typeArguments.map { substitute(it, callExpression) }
            )
        }
    }
}
