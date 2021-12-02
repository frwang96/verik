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
import io.verik.compiler.message.Messages

class ReferenceForwardingMap {

    private val referenceForwardingMap = HashMap<DeclarationBinding, EDeclaration>()

    operator fun set(
        oldDeclaration: EDeclaration,
        typeParameterContext: TypeParameterContext,
        newDeclaration: EDeclaration
    ) {
        referenceForwardingMap[DeclarationBinding(oldDeclaration, typeParameterContext)] = newDeclaration
    }

    operator fun get(
        declaration: EDeclaration,
        typeParameterContext: TypeParameterContext,
        element: EElement
    ): EDeclaration {
        val forwardedDeclaration = referenceForwardingMap[DeclarationBinding(declaration, typeParameterContext)]
        return if (forwardedDeclaration != null) {
            forwardedDeclaration
        } else {
            Messages.INTERNAL_ERROR.on(element, "Forwarded declaration not found: ${declaration.name}")
            declaration
        }
    }

    fun matchTypeParameterContexts(
        declaration: EDeclaration,
        typeParameterContext: TypeParameterContext
    ): List<TypeParameterContext> {
        val typeParameterContexts = ArrayList<TypeParameterContext>()
        referenceForwardingMap.keys.forEach {
            if (it.declaration == declaration && it.typeParameterContext.matches(typeParameterContext))
                typeParameterContexts.add(it.typeParameterContext)
        }
        return typeParameterContexts
    }

    fun contains(declarationBinding: DeclarationBinding): Boolean {
        return declarationBinding in referenceForwardingMap
    }
}
