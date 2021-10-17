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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypeParameter
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
        return typeParameter.toType()
    }

    companion object {

        val EMPTY = TypeParameterContext(listOf())

        fun get(
            typeArguments: List<Type>,
            typeParameterized: TypeParameterized,
            element: EElement
        ): TypeParameterContext? {
            typeArguments.forEach {
                if (!it.isSpecialized()) {
                    Messages.INTERNAL_ERROR.on(element, "Type argument not specialized: $it")
                    return null
                }
            }
            val expectedSize = typeParameterized.typeParameters.size
            val actualSize = typeArguments.size
            return if (expectedSize == actualSize) {
                val typeParameterBindings = typeParameterized.typeParameters
                    .zip(typeArguments)
                    .map { (typeParameter, type) -> TypeParameterBinding(typeParameter, type) }
                TypeParameterContext(typeParameterBindings)
            } else {
                Messages.INTERNAL_ERROR.on(
                    element,
                    "Mismatch in type parameters: Expected $expectedSize actual $actualSize"
                )
                null
            }
        }
    }
}
