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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.property.Type
import io.verik.compiler.message.Messages

data class TypeParameterContext(val typeParameterBindings: List<TypeParameterBinding>) {

    fun bind(type: Type, element: EElement) {
        val reference = type.reference
        if (reference is ETypeParameter) {
            typeParameterBindings.forEach { typeParameterBinding ->
                if (typeParameterBinding.typeParameter == reference) {
                    type.reference = typeParameterBinding.type.reference
                    type.arguments = ArrayList(typeParameterBinding.type.arguments.map { it.copy() })
                    return
                }
            }
            Messages.INTERNAL_ERROR.on(element, "Unable to bind type parameter ${reference.name}")
        } else {
            type.arguments.forEach { bind(it, element) }
        }
    }

    companion object {

        val EMPTY = TypeParameterContext(listOf())
    }
}
