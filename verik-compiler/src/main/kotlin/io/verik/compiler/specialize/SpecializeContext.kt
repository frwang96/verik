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
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.property.Type

class SpecializeContext {

    var typeParameterContext = TypeParameterContext.EMPTY

    private val referenceForwardingMap = ReferenceForwardingMap()

    operator fun set(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        referenceForwardingMap[oldDeclaration, typeParameterContext] = newDeclaration
    }

    operator fun get(declaration: EDeclaration, element: EElement): EDeclaration {
        return referenceForwardingMap[declaration, typeParameterContext, element]
    }

    operator fun get(
        declaration: EDeclaration,
        typeParameterContext: TypeParameterContext,
        element: EElement
    ): EDeclaration {
        return referenceForwardingMap[declaration, typeParameterContext, element]
    }

    fun matchTypeParameterContexts(
        declaration: EDeclaration,
        typeParameterContext: TypeParameterContext
    ): List<TypeParameterContext> {
        return referenceForwardingMap.matchTypeParameterContexts(declaration, typeParameterContext)
    }

    fun contains(declarationBinding: DeclarationBinding): Boolean {
        return referenceForwardingMap.contains(declarationBinding)
    }

    fun <E : EElement> specialize(element: E): E {
        return ElementSpecializer.specialize(element, this)
    }

    fun specializeType(typedElement: ETypedElement): Type {
        return TypeSpecializer.specialize(typedElement.type, this, typedElement, true)
    }

    fun specializeType(type: Type, element: EElement): Type {
        return TypeSpecializer.specialize(type, this, element, true)
    }
}
