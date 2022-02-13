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

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.message.Messages

class SpecializeContext {

    private val originalDeclarations = HashSet<EDeclaration>()
    private val referenceForwardingMap = HashMap<TypeParameterBinding, EDeclaration>()
    private val indexMap = HashMap<TypeParameterBinding, Int>()
    private var index = 0

    fun register(declaration: EDeclaration, typeArguments: List<Type>, copiedDeclaration: EDeclaration) {
        originalDeclarations.add(declaration)
        val typeParameterBinding = TypeParameterBinding(declaration, typeArguments)
        if (typeParameterBinding in referenceForwardingMap) {
            Messages.INTERNAL_ERROR.on(declaration, "Declaration has already been added to reference forwarding map")
        }
        referenceForwardingMap[typeParameterBinding] = copiedDeclaration
        indexMap[typeParameterBinding] = index++
    }

    fun contains(typeParameterBinding: TypeParameterBinding): Boolean {
        return typeParameterBinding in referenceForwardingMap
    }

    fun forward(declaration: Declaration, typeArguments: List<Type>, element: EElement): Declaration {
        if (declaration !is EDeclaration)
            return declaration
        return referenceForwardingMap[TypeParameterBinding(declaration, typeArguments)]
            ?: Messages.INTERNAL_ERROR.on(element, "Forwarded declaration not found: ${declaration.name}")
    }

    fun getTypeParameterBindings(declaration: EDeclaration): List<TypeParameterBinding> {
        val typeParameterBindings = ArrayList<Pair<Int, TypeParameterBinding>>()
        referenceForwardingMap.forEach { (typeParameterBinding, specializedDeclaration) ->
            if (typeParameterBinding.declaration == declaration) {
                val index = indexMap[typeParameterBinding]!!
                typeParameterBindings.add(
                    Pair(index, TypeParameterBinding(specializedDeclaration, typeParameterBinding.typeArguments))
                )
            }
        }
        return typeParameterBindings
            .sortedBy { it.first }
            .map { it.second }
    }

    fun getOriginalDeclarations(): Set<EDeclaration> {
        return originalDeclarations
    }
}
