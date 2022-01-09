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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.Type
import io.verik.compiler.message.Messages

data class TypeParameterContext(val typeParameterBindings: List<TypeParameterBinding>) {

    fun specialize(typeParameter: ETypeParameter, element: EElement): Type {
        typeParameterBindings.forEach {
            if (it.typeParameter == typeParameter)
                return it.type.copy()
        }
        Messages.INTERNAL_ERROR.on(element, "Unable to specialize type parameter ${typeParameter.name}")
    }

    fun matches(typeParameterContext: TypeParameterContext): Boolean {
        typeParameterContext.typeParameterBindings.forEach { typeParameterBinding ->
            val matchedTypeParameterBinding = typeParameterBindings.find {
                it.typeParameter == typeParameterBinding.typeParameter
            } ?: return false
            if (matchedTypeParameterBinding.type != typeParameterBinding.type)
                return false
        }
        return true
    }

    companion object {

        val EMPTY = TypeParameterContext(listOf())

        fun getFromTypeArguments(
            typeArguments: List<Type>,
            typeParameterized: TypeParameterized,
            specializeContext: SpecializeContext,
            element: EElement
        ): TypeParameterContext {
            val specializedTypeArguments = typeArguments.map {
                TypeSpecializer.specialize(it, specializeContext, element, false)
            }
            val expectedSize = typeParameterized.typeParameters.size
            val actualSize = specializedTypeArguments.size
            return if (expectedSize == actualSize) {
                val typeParameterBindings = typeParameterized.typeParameters
                    .zip(specializedTypeArguments)
                    .map { (typeParameter, type) -> TypeParameterBinding(typeParameter, type) }
                TypeParameterContext(typeParameterBindings)
            } else {
                Messages.INTERNAL_ERROR.on(
                    element,
                    "Mismatch in type parameters: Expected $expectedSize actual $actualSize"
                )
            }
        }

        fun getFromReceiver(
            receiverExpression: EReceiverExpression,
            specializeContext: SpecializeContext
        ): TypeParameterContext {
            val receiver = receiverExpression.receiver
            return if (receiver != null) {
                val specializedType = TypeSpecializer.specialize(
                    receiver.type,
                    specializeContext,
                    receiverExpression,
                    false
                )
                val specializedTypeReference = specializedType.reference
                if (specializedTypeReference is EKtClass) {
                    getFromTypeArguments(
                        specializedType.arguments,
                        specializedTypeReference,
                        specializeContext,
                        receiverExpression
                    )
                } else EMPTY
            } else {
                val reference = receiverExpression.reference
                if (reference is EDeclaration && reference !is EPrimaryConstructor && reference.parent !is EFile)
                    specializeContext.typeParameterContext
                else EMPTY
            }
        }
    }
}
